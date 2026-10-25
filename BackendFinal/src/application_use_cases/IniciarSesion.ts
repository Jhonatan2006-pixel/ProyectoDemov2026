import jwt from "jsonwebtoken";

import UsuarioModel from "../infrastructure/models/UsuarioModel";

// Respuesta de un inicio de sesión correcto: la usan el login con
// contraseña y el login biométrico, para que la app reciba lo mismo
export const respuestaInicioSesion = (usuario: UsuarioModel) => {

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

    return {
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
    };
};
