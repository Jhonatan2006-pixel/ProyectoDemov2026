"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const app_1 = __importDefault(require("./src/app"));
const sequelize_1 = require("./src/infrastructure/database/sequelize");
const models_1 = require("./src/infrastructure/models");
const PORT = 3000;
async function startServer() {
    try {
        (0, models_1.registerModels)();
        await (0, sequelize_1.connectDB)();
        app_1.default.listen(PORT, () => {
            console.log(`Servidor escuchando en el puerto ${PORT}`);
        });
    }
    catch (error) {
        console.error("Error al iniciar servidor:", error);
    }
}
startServer();
