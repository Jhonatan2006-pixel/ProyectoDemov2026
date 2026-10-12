"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const TareaController_1 = require("../controllers/TareaController");
const router = (0, express_1.Router)();
router.get("/api/tareas", TareaController_1.TareaController.getTareas);
router.post("/api/tareas", TareaController_1.TareaController.createTarea);
router.patch("/api/tareas/:id/estado", TareaController_1.TareaController.cambiarEstado);
exports.default = router;
