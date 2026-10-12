import { Router } from "express";
import { AuthController } from "../controllers/AuthController";
import { authMiddleware } from "../middlewares/authMiddleware";

const router = Router();

router.post("/register", AuthController.register);
router.post("/login", AuthController.login);
router.get("/profile", authMiddleware, AuthController.profile);

// Guardar o actualizar foto de perfil
router.post(
    "/profile/foto",
    authMiddleware,
    AuthController.guardarFoto
);

export default router;