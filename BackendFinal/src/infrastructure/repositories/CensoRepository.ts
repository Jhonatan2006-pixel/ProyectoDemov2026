import CensoModel from "../models/CensoModel";

export class CensoRepository {

    // Guardar un nuevo censo
    async create(censo: {
        nombre: string;
        apellido: string;
        total_miembros: number;
        cantidad_televisiones: number;
        latitud: number;
        longitud: number;
        usuario_id: number;
    }) {

        return await CensoModel.create({
            nombre: censo.nombre,
            apellido: censo.apellido,
            total_miembros: censo.total_miembros,
            cantidad_televisiones: censo.cantidad_televisiones,
            latitud: censo.latitud,
            longitud: censo.longitud,

            // Los censos creados desde la aplicación
            // siempre serán de carga NORMAL
            tipo_carga: "NORMAL",

            usuario_id: censo.usuario_id
        });
    }

    // Obtener todos los censos
    async findAll() {
        return await CensoModel.findAll({
            order: [["id", "DESC"]]
        });
    }

    // Obtener solamente censos de carga normal
    async findNormal() {
        return await CensoModel.findAll({
            where: {
                tipo_carga: "NORMAL"
            },
            order: [["id", "DESC"]]
        });
    }

    // Obtener solamente censos de carga masiva
    async findMasivos() {
        return await CensoModel.findAll({
            where: {
                tipo_carga: "MASIVA"
            },
            order: [["id", "DESC"]]
        });
    }
}