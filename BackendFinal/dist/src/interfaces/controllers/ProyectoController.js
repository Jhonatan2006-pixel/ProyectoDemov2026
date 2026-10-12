"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ProyectoController = void 0;
const ProyectoServiceImpl_1 = require("../../infrastructure/services/ProyectoServiceImpl");
const proyectoService = new ProyectoServiceImpl_1.ProyectoServiceImpl();
class ProyectoController {
    static async getProyectos(req, res) {
        try {
            const proyectos = await proyectoService.getProyectos();
            res.status(200).json(proyectos);
        }
        catch (error) {
            console.error("Error al obtener proyectos:", error);
            res.status(500).json({ mensaje: "Error al obtener proyectos" });
        }
    }
    static async createProyecto(req, res) {
        try {
            const { nombre, descripcion } = req.body;
            if (!nombre) {
                res.status(400).json({ mensaje: "El nombre del proyecto es obligatorio" });
                return;
            }
            const nuevoProyecto = await proyectoService.createProyecto({
                nombre,
                descripcion
            });
            res.status(201).json(nuevoProyecto);
        }
        catch (error) {
            console.error("Error al crear proyecto:", error);
            res.status(500).json({ mensaje: "Error al crear proyecto" });
        }
    }
}
exports.ProyectoController = ProyectoController;
