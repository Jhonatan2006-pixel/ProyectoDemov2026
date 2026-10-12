"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.UsuarioRepository = void 0;
const UsuarioModel_1 = __importDefault(require("../models/UsuarioModel"));
class UsuarioRepository {
    async create(user) {
        return await UsuarioModel_1.default.create(user);
    }
    async findByEmail(email) {
        return await UsuarioModel_1.default.findOne({
            where: { email }
        });
    }
    async findById(id) {
        return await UsuarioModel_1.default.findByPk(id);
    }
}
exports.UsuarioRepository = UsuarioRepository;
