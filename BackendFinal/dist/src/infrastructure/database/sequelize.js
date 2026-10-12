"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.connectDB = exports.sequelize = void 0;
const sequelize_1 = require("sequelize");
const dotenv_1 = __importDefault(require("dotenv"));
dotenv_1.default.config();
exports.sequelize = new sequelize_1.Sequelize(process.env.DB_NAME, process.env.DB_USER, process.env.DB_PASSWORD, {
    host: process.env.DB_HOST,
    dialect: "mssql",
    port: Number(process.env.DB_PORT) || 1433,
    logging: (sql) => console.log("[SQL]", sql),
    dialectOptions: {
        options: {
            encrypt: false,
            trustServerCertificate: true
        }
    }
});
const connectDB = async () => {
    try {
        await exports.sequelize.authenticate();
        console.log("Conexión a SQL Server exitosa");
    }
    catch (error) {
        console.error("Error al conectar con SQL Server:", error);
        throw error;
    }
};
exports.connectDB = connectDB;
