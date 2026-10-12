"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.TareaModel = void 0;
const sequelize_1 = require("sequelize");
const sequelize_2 = require("../database/sequelize");
class TareaModel extends sequelize_1.Model {
}
exports.TareaModel = TareaModel;
TareaModel.init({
    id: {
        type: sequelize_1.DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true
    },
    titulo: {
        type: sequelize_1.DataTypes.STRING(100),
        allowNull: false
    },
    descripcion: {
        type: sequelize_1.DataTypes.STRING(255),
        allowNull: true
    },
    estado: {
        type: sequelize_1.DataTypes.STRING(20),
        allowNull: false,
        defaultValue: "pendiente"
    },
    proyecto_id: {
        type: sequelize_1.DataTypes.INTEGER,
        allowNull: false
    },
    created_at: {
        type: sequelize_1.DataTypes.DATE,
        allowNull: true
    },
    updated_at: {
        type: sequelize_1.DataTypes.DATE,
        allowNull: true
    }
}, {
    sequelize: sequelize_2.sequelize,
    tableName: "tareas",
    schema: "dbo",
    timestamps: false
});
