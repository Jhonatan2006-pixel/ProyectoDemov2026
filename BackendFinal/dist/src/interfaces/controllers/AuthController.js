"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AuthController = void 0;
const bcryptjs_1 = __importDefault(require("bcryptjs"));
const jsonwebtoken_1 = __importDefault(require("jsonwebtoken"));
const UsuarioRepository_1 = require("../../infrastructure/repositories/UsuarioRepository");
const usuarioRepository = new UsuarioRepository_1.UsuarioRepository();
class AuthController {
    // ============================
    // REGISTRO
    // ============================
    static async register(req, res) {
        try {
            const { nombre, email, telefono, password } = req.body;
            // Validar campos obligatorios
            if (!nombre || !email || !password) {
                return res.status(400).json({
                    success: false,
                    message: "Nombre, correo y contraseña son obligatorios",
                    data: null
                });
            }
            // Verificar si el correo ya existe
            const usuarioExistente = await usuarioRepository.findByEmail(email);
            if (usuarioExistente) {
                return res.status(409).json({
                    success: false,
                    message: "El correo ya está registrado",
                    data: null
                });
            }
            // Encriptar contraseña
            const passwordHash = await bcryptjs_1.default.hash(password, 10);
            // Crear usuario
            const nuevoUsuario = await usuarioRepository.create({
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
                    telefono: nuevoUsuario.telefono
                }
            });
        }
        catch (error) {
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
    static async login(req, res) {
        try {
            const { email, password } = req.body;
            // Validar campos obligatorios
            if (!email || !password) {
                return res.status(400).json({
                    success: false,
                    message: "Correo y contraseña son obligatorios",
                    data: null
                });
            }
            // Buscar usuario por correo
            const usuario = await usuarioRepository.findByEmail(email);
            if (!usuario) {
                return res.status(401).json({
                    success: false,
                    message: "Correo o contraseña incorrectos",
                    data: null
                });
            }
            // Comparar contraseña con el hash guardado
            const passwordCorrecta = await bcryptjs_1.default.compare(password, usuario.password);
            if (!passwordCorrecta) {
                return res.status(401).json({
                    success: false,
                    message: "Correo o contraseña incorrectos",
                    data: null
                });
            }
            // Generar token JWT
            const token = jsonwebtoken_1.default.sign({
                id: usuario.id,
                email: usuario.email
            }, process.env.JWT_SECRET, {
                expiresIn: "2h"
            });
            // Login correcto
            return res.status(200).json({
                success: true,
                message: "Inicio de sesión correcto",
                data: {
                    id: usuario.id,
                    nombre: usuario.nombre,
                    email: usuario.email,
                    telefono: usuario.telefono
                },
                token
            });
        }
        catch (error) {
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
    static async profile(req, res) {
        try {
            const user = req.user;
            if (!user || !user.id) {
                return res.status(401).json({
                    success: false,
                    message: "Usuario no autenticado",
                    data: null
                });
            }
            const usuario = await usuarioRepository.findById(user.id);
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
                    telefono: usuario.telefono
                }
            });
        }
        catch (error) {
            console.error("Error en profile:", error);
            return res.status(500).json({
                success: false,
                message: "Error interno del servidor",
                data: null
            });
        }
    }
}
exports.AuthController = AuthController;
