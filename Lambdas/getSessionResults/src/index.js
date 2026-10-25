const { getLivenessResults } = require('./services/rekognition');
const { buildResponse } = require('./utils/response');

// Recibe el sessionId que creo la Lambda getSessionId y devuelve
// el resultado de la prueba de vida que hizo el usuario en el dispositivo
exports.handler = async (event) => {
    try {
        const { sessionId } = event || {};

        if (!sessionId) {
            return buildResponse(400, 'Se requiere el sessionId');
        }

        const resultado = await getLivenessResults(sessionId);
        console.log('Estado:', resultado.status, 'Confianza:', resultado.confidence);
        return buildResponse(200, resultado);
    } catch (error) {
        console.log('Error:', error);
        return buildResponse(500, 'Error al obtener el resultado de la sesion');
    }
};
