import express from "express";
import cors from "cors";

import authRoutes from "./interfaces/routes/authRoutes";
import censoRoutes from "./interfaces/routes/censoRoutes";

const app = express();

app.use(cors());
// El límite por defecto (100kb) no alcanza para la foto de perfil en Base64
app.use(express.json({ limit: "10mb" }));

app.use("/api/auth", authRoutes);
app.use("/api/censos", censoRoutes);

export default app;