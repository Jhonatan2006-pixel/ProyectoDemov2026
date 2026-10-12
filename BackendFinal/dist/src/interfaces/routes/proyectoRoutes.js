"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const ProyectoController_1 = require("../controllers/ProyectoController");
const router = (0, express_1.Router)();
router.get("/api/proyectos", ProyectoController_1.ProyectoController.getProyectos);
router.post("/api/proyectos", ProyectoController_1.ProyectoController.createProyecto);
exports.default = router;
