import { Router } from "express";
import { CensoController } from "../controllers/CensoController";
import { authMiddleware } from "../middlewares/authMiddleware";
import {
    soloCensista,
    soloAdmin
} from "../middlewares/roleMiddleware";

const router = Router();

// Crear censo:
// solamente puede hacerlo un CENSISTA
router.post(
    "/",
    authMiddleware,
    soloCensista,
    CensoController.crear
);

// Ver todos los censos:
// solamente puede hacerlo un ADMIN
router.get(
    "/",
    authMiddleware,
    soloAdmin,
    CensoController.obtenerTodos
);

// Ver censos de carga masiva:
// solamente puede hacerlo un ADMIN
router.get(
    "/masivos",
    authMiddleware,
    soloAdmin,
    CensoController.obtenerMasivos
);

export default router;