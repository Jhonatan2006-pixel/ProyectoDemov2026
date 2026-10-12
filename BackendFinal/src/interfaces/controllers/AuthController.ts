import { Request, Response } from "express";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";

import { UsuarioRepository } from "../../infrastructure/repositories/UsuarioRepository";

const usuarioRepository = new UsuarioRepository();

export class AuthController {

    // ============================
    // REGISTRO
    // ============================
    static async register(req: Request, res: Response) {

        try {

            const {
                nombre,
                email,
                telefono,
                password
            } = req.body;

            // Validar campos obligatorios
            if (!nombre || !email || !password) {
                return res.status(400).json({
                    success: false,
                    message: "Nombre, correo y contraseña son obligatorios",
                    data: null
                });
            }

            // Verificar si el correo ya existe
            const usuarioExistente =
                await usuarioRepository.findByEmail(email);

            if (usuarioExistente) {
                return res.status(409).json({
                    success: false,
                    message: "El correo ya está registrado",
                    data: null
                });
            }

            // Encriptar contraseña
            const passwordHash =
                await bcrypt.hash(password, 10);

            // Crear usuario
            const nuevoUsuario =
                await usuarioRepository.create({
                    nombre,
                    email,
                    telefono,
                    password: passwordHash
                });

            return res.status(201).json({
                success: true,
                message: "Usuario registrado correctamente",
                data: {
    id: nuevoUsuario.id,
    nombre: nuevoUsuario.nombre,
    email: nuevoUsuario.email,
    telefono: nuevoUsuario.telefono,
    rol: nuevoUsuario.rol
}
            });

        } catch (error) {

            console.error("Error en register:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno del servidor",
                data: null
            });
        }
    }


    // ============================
    // LOGIN
    // ============================
    static async login(req: Request, res: Response) {

        try {

            const {
                email,
                password
            } = req.body;

            // Validar campos obligatorios
            if (!email || !password) {
                return res.status(400).json({
                    success: false,
                    message: "Correo y contraseña son obligatorios",
                    data: null
                });
            }

            // Buscar usuario por correo
            const usuario =
                await usuarioRepository.findByEmail(email);

            if (!usuario) {
                return res.status(401).json({
                    success: false,
                    message: "Correo o contraseña incorrectos",
                    data: null
                });
            }

            // Comparar contraseña con el hash guardado
            const passwordCorrecta =
                await bcrypt.compare(password, usuario.password);

            if (!passwordCorrecta) {
                return res.status(401).json({
                    success: false,
                    message: "Correo o contraseña incorrectos",
                    data: null
                });
            }

            // Generar token JWT
            const token = jwt.sign(
    {
        id: usuario.id,
        email: usuario.email,
        rol: usuario.rol
    },
    process.env.JWT_SECRET as string,
    {
        expiresIn: "2h"
    }
);
            // Login correcto
            return res.status(200).json({
                success: true,
                message: "Inicio de sesión correcto",
                data: {
                    id: usuario.id,
                    nombre: usuario.nombre,
                    email: usuario.email,
                    telefono: usuario.telefono,
                    rol: usuario.rol
        },
        token
            });

        } catch (error) {

            console.error("Error en login:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno del servidor",
                data: null
            });
        }
    }

        // ============================
    // PERFIL
    // ============================
    static async profile(req: Request, res: Response) {

        try {

            const user = (req as any).user;

            if (!user || !user.id) {
                return res.status(401).json({
                    success: false,
                    message: "Usuario no autenticado",
                    data: null
                });
            }

            const usuario =
                await usuarioRepository.findById(user.id);

            if (!usuario) {
                return res.status(404).json({
                    success: false,
                    message: "Usuario no encontrado",
                    data: null
                });
            }

            return res.status(200).json({
    success: true,
    message: "Perfil obtenido correctamente",
    data: {
        id: usuario.id,
        nombre: usuario.nombre,
        email: usuario.email,
        telefono: usuario.telefono,
        rol: usuario.rol,

        foto: usuario.foto
            ? usuario.foto.toString("base64")
            : null
    }
});

        } catch (error) {

            console.error("Error en profile:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno del servidor",
                data: null
            });
        }
    }

    // ============================
// GUARDAR FOTO DE PERFIL
// ============================
static async guardarFoto(req: Request, res: Response) {

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

        // La foto llegará desde Android como Base64
        const { foto } = req.body;

        if (!foto) {
            return res.status(400).json({
                success: false,
                message: "No se recibió ninguna foto",
                data: null
            });
        }

        // Convertir Base64 a datos binarios (Buffer)
        const fotoBuffer = Buffer.from(foto, "base64");

        // Guardar la foto del usuario autenticado
        const usuarioActualizado =
            await usuarioRepository.guardarFoto(
                user.id,
                fotoBuffer
            );

        if (!usuarioActualizado) {
            return res.status(404).json({
                success: false,
                message: "Usuario no encontrado",
                data: null
            });
        }

        return res.status(200).json({
            success: true,
            message: "Foto de perfil guardada correctamente",
            data: null
        });

    } catch (error) {

        console.error("Error en guardarFoto:", error);

        return res.status(500).json({
            success: false,
            message: "Error al guardar la foto de perfil",
            data: null
        });
    }
}


}