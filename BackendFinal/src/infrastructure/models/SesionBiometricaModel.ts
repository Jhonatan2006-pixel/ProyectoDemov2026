import { DataTypes, Model } from "sequelize";
import { sequelize } from "../database/sequelize";

// Cada intento de inicio de sesión biométrico:
//   tipo ROSTRO -> comparación de la selfie con la foto de perfil
//   tipo VIDA   -> prueba de vida (Face Liveness) + comparación
// resultado: EXITO (onSuccess) o ERROR (onError)
class SesionBiometricaModel extends Model {
    public id!: number;
    public session_id!: string;
    public usuario_id!: number;
    public tipo!: string;
    public estado!: string;
    public confianza!: number | null;
    public similitud!: number | null;
    public resultado!: string | null;
    public mensaje!: string | null;
    public created_at!: Date;
    public updated_at!: Date | null;
}

SesionBiometricaModel.init(
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true
        },

        session_id: {
            type: DataTypes.STRING(64),
            allowNull: false,
            unique: true
        },

        usuario_id: {
            type: DataTypes.INTEGER,
            allowNull: false
        },

        tipo: {
            type: DataTypes.STRING(10),
            allowNull: false
        },

        estado: {
            type: DataTypes.STRING(20),
            allowNull: false,
            defaultValue: "CREADA"
        },

        confianza: {
            type: DataTypes.DECIMAL(5, 2),
            allowNull: true
        },

        similitud: {
            type: DataTypes.DECIMAL(5, 2),
            allowNull: true
        },

        resultado: {
            type: DataTypes.STRING(10),
            allowNull: true
        },

        mensaje: {
            type: DataTypes.STRING(255),
            allowNull: true
        },

        created_at: {
            type: DataTypes.DATE,
            allowNull: false,
            defaultValue: DataTypes.NOW
        },

        updated_at: {
            type: DataTypes.DATE,
            allowNull: true
        }
    },
    {
        sequelize,
        tableName: "sesiones_biometricas",
        schema: "dbo",
        timestamps: false
    }
);

export default SesionBiometricaModel;
