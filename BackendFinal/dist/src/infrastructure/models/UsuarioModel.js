"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const sequelize_1 = require("sequelize");
const sequelize_2 = require("../database/sequelize");
class UsuarioModel extends sequelize_1.Model {
}
UsuarioModel.init({
    id: {
        type: sequelize_1.DataTypes.INTEGER,
        autoIncrement: true,
        primaryKey: true
    },
    nombre: {
        type: sequelize_1.DataTypes.STRING(100),
        allowNull: false
    },
    email: {
        type: sequelize_1.DataTypes.STRING(150),
        allowNull: false,
        unique: true
    },
    telefono: {
        type: sequelize_1.DataTypes.STRING(20),
        allowNull: true
    },
    password: {
        type: sequelize_1.DataTypes.STRING(255),
        allowNull: false
    },
    created_at: {
        type: sequelize_1.DataTypes.DATE,
        allowNull: true
    }
}, {
    sequelize: sequelize_2.sequelize,
    tableName: "usuarios",
    schema: "dbo",
    timestamps: false
});
exports.default = UsuarioModel;
