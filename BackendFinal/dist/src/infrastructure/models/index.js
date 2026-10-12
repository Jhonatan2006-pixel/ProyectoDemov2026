"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.registerModels = registerModels;
const ProyectoModel_1 = require("./ProyectoModel");
const TareaModel_1 = require("./TareaModel");
function registerModels() {
    ProyectoModel_1.ProyectoModel.hasMany(TareaModel_1.TareaModel, {
        foreignKey: "proyecto_id",
        as: "tareas"
    });
    TareaModel_1.TareaModel.belongsTo(ProyectoModel_1.ProyectoModel, {
        foreignKey: "proyecto_id",
        as: "proyecto"
    });
}
