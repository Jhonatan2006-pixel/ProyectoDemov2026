import app from "./src/app";
import { connectDB } from "./src/infrastructure/database/sequelize";

const PORT = Number(process.env.PORT) || 3000;

async function startServer() {
  try {
    await connectDB();

    app.listen(PORT, "0.0.0.0", () => {
      console.log(`Servidor escuchando en el puerto ${PORT}`);
    });
  } catch (error) {
    console.error("Error al iniciar servidor:", error);
  }
}

startServer();