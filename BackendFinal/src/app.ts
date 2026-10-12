import express from "express";
import cors from "cors";

import authRoutes from "./interfaces/routes/authRoutes";
import censoRoutes from "./interfaces/routes/censoRoutes";

const app = express();

app.use(cors());
app.use(express.json());

app.use("/api/auth", authRoutes);
app.use("/api/censos", censoRoutes);

export default app;