"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ProyectoServiceImpl = void 0;
const ProyectoModel_1 = require("../models/ProyectoModel");
class ProyectoServiceImpl {
    async getProyectos() {
        return await ProyectoModel_1.ProyectoModel.findAll({
            order: [["id", "DESC"]]
        });
    }
    async createProyecto(data) {
        return await ProyectoModel_1.ProyectoModel.create({
            nombre: data.nombre,
            descripcion: data.descripcion || null
        });
    }
}
exports.ProyectoServiceImpl = ProyectoServiceImpl;
