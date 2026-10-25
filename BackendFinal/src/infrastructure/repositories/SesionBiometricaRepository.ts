import SesionBiometricaModel from "../models/SesionBiometricaModel";

export class SesionBiometricaRepository {

    async create(datos: {
        session_id: string;
        usuario_id: number;
        tipo: string;
        estado?: string;
    }) {
        return await SesionBiometricaModel.create(datos as any);
    }

    async findBySessionId(sessionId: string) {
        return await SesionBiometricaModel.findOne({
            where: { session_id: sessionId }
        });
    }

    // Guardar el resultado de la sesión (onSuccess / onError)
    async guardarResultado(
        sesion: SesionBiometricaModel,
        datos: {
            estado?: string;
            confianza?: number | null;
            similitud?: number | null;
            resultado: "EXITO" | "ERROR";
            mensaje: string;
        }
    ) {
        Object.assign(sesion, datos, { updated_at: new Date() });

        await sesion.save();

        return sesion;
    }
}
