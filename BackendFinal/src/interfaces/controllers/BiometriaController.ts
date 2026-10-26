import { Request, Response } from "express";
import { randomUUID } from "crypto";

import { respuestaInicioSesion } from "../../application_use_cases/IniciarSesion";
import { invocarLambda, LAMBDAS } from "../../infrastructure/aws/LambdaService";
import UsuarioModel from "../../infrastructure/models/UsuarioModel";
import SesionBiometricaModel from "../../infrastructure/models/SesionBiometricaModel";
import { SesionBiometricaRepository } from "../../infrastructure/repositories/SesionBiometricaRepository";
import { UsuarioRepository } from "../../infrastructure/repositories/UsuarioRepository";

const usuarioRepository = new UsuarioRepository();
const sesionRepository = new SesionBiometricaRepository();

// Confianza mínima (0 a 100) para aceptar que es una persona real
const CONFIANZA_MINIMA_VIDA = 80;

const SERVICIO_NO_DISPONIBLE =
    "El servicio de reconocimiento facial no está disponible";

// Resultado de comparar una foto con la foto de perfil del usuario
interface Comparacion {
    status: number;
    message: string;
    similitud: number | null;
}

// Llama a la Lambda compareFaces con la foto de perfil (guardada en la
// base de datos) y la foto nueva. Devuelve status 200 si coinciden.
const compararConFotoDePerfil = async (
    usuario: UsuarioModel,
    fotoBase64: string
): Promise<Comparacion> => {

    if (!usuario.foto) {
        return { status: 400, message: "El usuario no tiene foto de perfil", similitud: null };
    }

    let respuesta;

    try {
        respuesta = await invocarLambda(LAMBDAS.compareFaces(), {
            sourceImage: usuario.foto.toString("base64"),
            targetImage: fotoBase64
        });
    } catch (error) {
        console.error("Error al invocar compareFaces:", error);
        return { status: 503, message: SERVICIO_NO_DISPONIBLE, similitud: null };
    }

    if (respuesta.statusCode === 400) {
        return { status: 400, message: "No se detectó un rostro en la foto", similitud: null };
    }

    if (respuesta.statusCode !== 200) {
        return { status: 503, message: SERVICIO_NO_DISPONIBLE, similitud: null };
    }

    const { coincide, similitud } = respuesta.body;

    return coincide
        ? { status: 200, message: "Inicio de sesión correcto", similitud }
        : { status: 401, message: "El rostro no coincide con la foto de perfil", similitud };
};

// Guarda el resultado de la sesión y responde a la app
const terminarSesion = async (
    res: Response,
    sesion: SesionBiometricaModel,
    usuario: UsuarioModel,
    comparacion: Comparacion,
    extra: { estado?: string; confianza?: number | null } = {}
) => {
    const exito = comparacion.status === 200;

    await sesionRepository.guardarResultado(sesion, {
        ...extra,
        similitud: comparacion.similitud,
        resultado: exito ? "EXITO" : "ERROR",
        mensaje: comparacion.message
    });

    if (exito) {
        return res.status(200).json(respuestaInicioSesion(usuario));
    }

    return res.status(comparacion.status).json({
        success: false,
        message: comparacion.message,
        data: null
    });
};

const errorInterno = (res: Response, donde: string, error: unknown) => {

    console.error(`Error en ${donde}:`, error);

    return res.status(500).json({
        success: false,
        message: "Error interno del servidor",
        data: null
    });
};

export class BiometriaController {

    // ============================
    // LOGIN CON ROSTRO
    // La app envía el correo y una selfie; se compara con la foto de perfil
    // ============================
    static async loginFacial(req: Request, res: Response) {

        try {

            const { email, foto } = req.body;

            if (!email || !foto) {
                return res.status(400).json({
                    success: false,
                    message: "No se recibió ninguna foto",
                    data: null
                });
            }

            const usuario = await usuarioRepository.findByEmail(email);

            if (!usuario) {
                return res.status(404).json({
                    success: false,
                    message: "Usuario no encontrado",
                    data: null
                });
            }

            // Cada intento queda registrado con su propio UUID
            const sesion = await sesionRepository.create({
                session_id: randomUUID(),
                usuario_id: usuario.id,
                tipo: "ROSTRO"
            });

            const comparacion = await compararConFotoDePerfil(usuario, foto);

            return await terminarSesion(res, sesion, usuario, comparacion, {
                estado: "COMPARADA"
            });

        } catch (error) {
            return errorInterno(res, "loginFacial", error);
        }
    }

    // ============================
    // PRUEBA DE VIDA - PASO 1
    // Lambda getSessionId: crea la sesión de Face Liveness y devuelve su
    // UUID, que la app le pasa a Amplify para grabar el rostro
    // ============================
    static async crearSesionVida(req: Request, res: Response) {

        try {

            const { email } = req.body;

            const usuario = email
                ? await usuarioRepository.findByEmail(email)
                : null;

            if (!usuario) {
                return res.status(404).json({
                    success: false,
                    message: "Usuario no encontrado",
                    data: null
                });
            }

            if (!usuario.foto) {
                return res.status(400).json({
                    success: false,
                    message: "El usuario no tiene foto de perfil",
                    data: null
                });
            }

            let respuesta;

            try {
                respuesta = await invocarLambda(LAMBDAS.getSessionId(), {});
            } catch (error) {
                console.error("Error al invocar getSessionId:", error);
                respuesta = null;
            }

            if (!respuesta || respuesta.statusCode !== 200) {
                return res.status(503).json({
                    success: false,
                    message: SERVICIO_NO_DISPONIBLE,
                    data: null
                });
            }

            const sessionId = String(respuesta.body);

            await sesionRepository.create({
                session_id: sessionId,
                usuario_id: usuario.id,
                tipo: "VIDA"
            });

            return res.status(201).json({
                success: true,
                message: "Sesión de prueba de vida creada",
                data: { sessionId }
            });

        } catch (error) {
            return errorInterno(res, "crearSesionVida", error);
        }
    }

    // ============================
    // PRUEBA DE VIDA - PASO 2
    // Cuando Amplify termina, la app envía el sessionId. La Lambda
    // getSessionResults dice si era una persona real y devuelve la imagen
    // de referencia, que se compara con la foto de perfil (compareFaces)
    // ============================
    static async resultadoVida(req: Request, res: Response) {

        try {

            const { sessionId } = req.body;

            const sesion = sessionId
                ? await sesionRepository.findBySessionId(String(sessionId))
                : null;

            if (!sesion || sesion.tipo !== "VIDA") {
                return res.status(404).json({
                    success: false,
                    message: "Sesión de prueba de vida no encontrada",
                    data: null
                });
            }

            // Un sessionId solo sirve para un intento
            if (sesion.resultado) {
                return res.status(409).json({
                    success: false,
                    message: "La sesión de prueba de vida ya fue usada",
                    data: null
                });
            }

            const usuario = await usuarioRepository.findById(sesion.usuario_id);

            if (!usuario) {
                return res.status(404).json({
                    success: false,
                    message: "Usuario no encontrado",
                    data: null
                });
            }

            let respuesta;

            try {
                // La Lambda del laboratorio espera una petición HTTP:
                // el sessionId viaja como texto JSON dentro de "body"
                respuesta = await invocarLambda(LAMBDAS.getSessionResults(), {
                    httpMethod: "POST",
                    body: JSON.stringify({ sessionId: sesion.session_id })
                });
            } catch (error) {
                console.error("Error al invocar getSessionResults:", error);
                respuesta = null;
            }

            if (!respuesta || respuesta.statusCode !== 200) {
                return await terminarSesion(res, sesion, usuario, {
                    status: 503,
                    message: SERVICIO_NO_DISPONIBLE,
                    similitud: null
                });
            }

            // Cuando falta un dato, la Lambda devuelve un texto en su lugar
            const { status, confidence, referenceImageBase64 } = respuesta.body;
            const confianza = typeof confidence === "number"
                ? Number(confidence.toFixed(2))
                : null;
            const estado = typeof status === "string"
                ? status.slice(0, 20)
                : "DESCONOCIDO";

            const esPersonaReal = status === "SUCCEEDED" &&
                confianza !== null &&
                confianza >= CONFIANZA_MINIMA_VIDA &&
                referenceImageBase64 !== "No se encontró imagen de referencia";

            if (!esPersonaReal) {
                return await terminarSesion(res, sesion, usuario, {
                    status: 401,
                    message: "No se superó la prueba de vida",
                    similitud: null
                }, { estado, confianza });
            }

            const comparacion =
                await compararConFotoDePerfil(usuario, referenceImageBase64);

            return await terminarSesion(res, sesion, usuario, comparacion, {
                estado,
                confianza
            });

        } catch (error) {
            return errorInterno(res, "resultadoVida", error);
        }
    }
}
