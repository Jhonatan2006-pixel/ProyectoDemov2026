import UsuarioModel from "../models/UsuarioModel";
import { User } from "../../domain/User";

export class UsuarioRepository {

    async create(user: User) {
        return await UsuarioModel.create(user as any);
    }

    async findByEmail(email: string) {
        return await UsuarioModel.findOne({
            where: { email }
        });
    }

    async findById(id: number) {
        return await UsuarioModel.findByPk(id);
    }

    // Guardar o actualizar la foto de perfil del usuario
    async guardarFoto(id: number, foto: Buffer) {

        const usuario = await UsuarioModel.findByPk(id);

        if (!usuario) {
            return null;
        }

        usuario.foto = foto;

        await usuario.save();

        return usuario;
    }
}