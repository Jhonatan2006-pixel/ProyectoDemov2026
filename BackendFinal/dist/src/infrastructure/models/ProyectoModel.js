"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.ProyectoModel = void 0;
const sequelize_1 = require("sequelize");
const sequelize_2 = require("../database/sequelize");
class ProyectoModel extends sequelize_1.Model {
}
exports.ProyectoModel = ProyectoModel;
ProyectoModel.init({
    id: {
        type: sequelize_1.DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true
    },
    nombre: {
        type: sequelize_1.DataTypes.STRING(100),
        allowNull: false
    },
    descripcion: {
        type: sequelize_1.DataTypes.STRING(255),
        allowNull: true
    }
}, {
    sequelize: sequelize_2.sequelize,
    tableName: "proyectos",
    schema: "dbo",
    timestamps: false
});
