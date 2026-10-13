import { Router } from "express";
import { AuthController } from "../controllers/AuthController";
import { authMiddleware } from "../middlewares/authMiddleware";

const router = Router();

router.post("/register", AuthController.register);
router.post("/login", AuthController.login);

// Comprobar el código antes de registrarse como administrador
router.post(
    "/verificar-codigo-admin",
    AuthController.verificarCodigoAdmin
);
router.get("/profile", authMiddleware, AuthController.profile);

// Guardar o actualizar foto de perfil
router.post(
    "/profile/foto",
    authMiddleware,
    AuthController.guardarFoto
);

export default router;