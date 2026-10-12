"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.TareaServiceImpl = void 0;
const TareaModel_1 = require("../models/TareaModel");
const ProyectoModel_1 = require("../models/ProyectoModel");
class TareaServiceImpl {
    async getTareas(estado) {
        const where = {};
        if (estado && estado !== "todas") {
            where.estado = estado;
        }
        return await TareaModel_1.TareaModel.findAll({
            where,
            include: [
                {
                    model: ProyectoModel_1.ProyectoModel,
                    as: "proyecto"
                }
            ],
            order: [["id", "DESC"]]
        });
    }
    async createTarea(data) {
        return await TareaModel_1.TareaModel.create({
            titulo: data.titulo,
            descripcion: data.descripcion || null,
            estado: data.estado || "pendiente",
            proyecto_id: data.proyecto_id
        });
    }
    async cambiarEstado(id, estado) {
        const tarea = await TareaModel_1.TareaModel.findByPk(id);
        if (!tarea) {
            return null;
        }
        tarea.estado = estado;
        await tarea.save();
        return tarea;
    }
}
exports.TareaServiceImpl = TareaServiceImpl;
