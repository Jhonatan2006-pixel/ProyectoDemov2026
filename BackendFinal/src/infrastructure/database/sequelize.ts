import { Dialect, Sequelize } from "sequelize";
import dotenv from "dotenv";

dotenv.config();

// Motor de base de datos, definido en el archivo .env:
//   mssql    -> SQL Server (computadora local). Es el valor por defecto.
//   postgres -> PostgreSQL (servidor de AWS).
const dialect = (process.env.DB_DIALECT || "mssql") as Dialect;

const puertoPorDefecto = dialect === "postgres" ? 5432 : 1433;

export const sequelize = new Sequelize(
  process.env.DB_NAME as string,
  process.env.DB_USER as string,
  process.env.DB_PASSWORD as string,
  {
    host: process.env.DB_HOST,
    dialect,
    port: Number(process.env.DB_PORT) || puertoPorDefecto,

    // En el servidor se desactiva con DB_LOGGING=false para no
    // llenar el registro con cada consulta
    logging: process.env.DB_LOGGING === "false"
      ? false
      : (sql) => console.log("[SQL]", sql),

    // Estas opciones solo existen para SQL Server
    dialectOptions: dialect === "mssql"
      ? {
          options: {
            encrypt: false,
            trustServerCertificate: true
          }
        }
      : {}
  }
);

export const connectDB = async () => {
  try {
    await sequelize.authenticate();
    console.log(`Conexión a la base de datos exitosa (${dialect})`);
  } catch (error) {
    console.error("Error al conectar con la base de datos:", error);
    throw error;
  }
};
