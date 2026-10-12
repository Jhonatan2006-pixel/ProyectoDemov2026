import { DataTypes, Model } from "sequelize";
import { sequelize } from "../database/sequelize";

class UsuarioModel extends Model {
    public id!: number;
    public nombre!: string;
    public email!: string;
    public telefono!: string;
    public password!: string;
    public foto!: Buffer | null;
    public rol!: string;
    public created_at!: Date;
}

UsuarioModel.init(
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

        email: {
            type: DataTypes.STRING(150),
            allowNull: false,
            unique: true
        },

        telefono: {
            type: DataTypes.STRING(20),
            allowNull: true
        },

        password: {
            type: DataTypes.STRING(255),
            allowNull: false
        },

        foto: {
            type: DataTypes.BLOB,
            allowNull: true
        },

        rol: {
            type: DataTypes.STRING(20),
            allowNull: false,
            defaultValue: "CENSISTA"
        },

        created_at: {
            type: DataTypes.DATE,
            allowNull: true
        }
    },
    {
        sequelize,
        tableName: "usuarios",
        schema: "dbo",
        timestamps: false
    }
);

export default UsuarioModel;