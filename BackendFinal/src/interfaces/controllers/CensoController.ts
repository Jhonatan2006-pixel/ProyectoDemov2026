import { Request, Response } from "express";
import { CensoRepository } from "../../infrastructure/repositories/CensoRepository";

const censoRepository = new CensoRepository();

export class CensoController {

    // ==========================================
    // CREAR CENSO - SOLO CENSISTA
    // ==========================================
    static async crear(req: Request, res: Response) {

        try {

            // Usuario obtenido desde el token JWT
            const user = (req as any).user;

            if (!user || !user.id) {
                return res.status(401).json({
                    success: false,
                    message: "Usuario no autenticado",
                    data: null
                });
            }

            const {
                nombre,
                apellido,
                total_miembros,
                cantidad_televisiones,
                latitud,
                longitud
            } = req.body;

            // Validar campos obligatorios
            if (
                !nombre ||
                !apellido ||
                total_miembros === undefined ||
                cantidad_televisiones === undefined ||
                latitud === undefined ||
                longitud === undefined
            ) {
                return res.status(400).json({
                    success: false,
                    message: "Todos los datos del censo son obligatorios",
                    data: null
                });
            }

            // Validar cantidades
            if (
                Number(total_miembros) <= 0 ||
                Number(cantidad_televisiones) < 0
            ) {
                return res.status(400).json({
                    success: false,
                    message: "Las cantidades ingresadas no son válidas",
                    data: null
                });
            }

            // Guardar el censo
            const nuevoCenso = await censoRepository.create({
                nombre,
                apellido,
                total_miembros: Number(total_miembros),
                cantidad_televisiones: Number(cantidad_televisiones),
                latitud: Number(latitud),
                longitud: Number(longitud),
                usuario_id: user.id
            });

            return res.status(201).json({
                success: true,
                message: "Censo registrado correctamente",
                data: nuevoCenso
            });

        } catch (error) {

            console.error("Error al crear censo:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno al registrar el censo",
                data: null
            });
        }
    }


    // ==========================================
    // OBTENER TODOS LOS CENSOS
    // ==========================================
    static async obtenerTodos(req: Request, res: Response) {

        try {

            const censos = await censoRepository.findAll();

            return res.status(200).json({
                success: true,
                message: "Censos obtenidos correctamente",
                data: censos
            });

        } catch (error) {

            console.error("Error al obtener censos:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno al obtener los censos",
                data: null
            });
        }
    }


    // ==========================================
    // OBTENER CENSOS MASIVOS
    // ==========================================
    static async obtenerMasivos(req: Request, res: Response) {

        try {

            const censos = await censoRepository.findMasivos();

            return res.status(200).json({
                success: true,
                message: "Censos masivos obtenidos correctamente",
                data: censos
            });

        } catch (error) {

            console.error("Error al obtener censos masivos:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno al obtener los censos masivos",
                data: null
            });
        }
    }
}