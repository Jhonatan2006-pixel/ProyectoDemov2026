import { Sequelize } from "sequelize";
import dotenv from "dotenv";

dotenv.config();

export const sequelize = new Sequelize(
  process.env.DB_NAME as string,
  process.env.DB_USER as string,
  process.env.DB_PASSWORD as string,
  {
    host: process.env.DB_HOST,
    dialect: "mssql",
    port: Number(process.env.DB_PORT) || 1433,
    logging: (sql) => console.log("[SQL]", sql),
    dialectOptions: {
      options: {
        encrypt: false,
        trustServerCertificate: true
      }
    }
  }
);

export const connectDB = async () => {
  try {
    await sequelize.authenticate();
    console.log("Conexión a SQL Server exitosa");
  } catch (error) {
    console.error("Error al conectar con SQL Server:", error);
    throw error;
  }
};