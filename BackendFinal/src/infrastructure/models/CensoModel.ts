import { DataTypes, Model } from "sequelize";
import { sequelize } from "../database/sequelize";

class CensoModel extends Model {
    public id!: number;
    public nombre!: string;
    public apellido!: string;
    public total_miembros!: number;
    public cantidad_televisiones!: number;
    public latitud!: number;
    public longitud!: number;
    public tipo_carga!: string;
    public usuario_id!: number | null;
    public created_at!: Date;
}

CensoModel.init(
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true
        },

        nombre: {
            type: DataTypes.STRING(100),
            allowNull: false
        },

        apellido: {
            type: DataTypes.STRING(100),
            allowNull: false
        },

        total_miembros: {
            type: DataTypes.INTEGER,
            allowNull: false
        },

        cantidad_televisiones: {
            type: DataTypes.INTEGER,
            allowNull: false
        },

        // PostgreSQL devuelve los DECIMAL como texto y SQL Server como
        // número: el get() hace que la API siempre responda un número
        latitud: {
            type: DataTypes.DECIMAL(10, 7),
            allowNull: false,
            get() {
                return Number(this.getDataValue("latitud"));
            }
        },

        longitud: {
            type: DataTypes.DECIMAL(10, 7),
            allowNull: false,
            get() {
                return Number(this.getDataValue("longitud"));
            }
        },

        tipo_carga: {
            type: DataTypes.STRING(10),
            allowNull: false,
            defaultValue: "NORMAL"
        },

        usuario_id: {
            type: DataTypes.INTEGER,
            allowNull: true
        },

        created_at: {
            type: DataTypes.DATE,
            allowNull: true
        }
    },
    {
        sequelize,
        tableName: "censos",
        schema: "dbo",
        timestamps: false
    }
);

export default CensoModel;