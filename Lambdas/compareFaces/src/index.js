const { compareFaces } = require('./services/rekognition');
const { buildResponse } = require('./utils/response');

// Recibe dos fotos en Base64:
//   sourceImage: la foto de perfil guardada en la base de datos
//   targetImage: la selfie que se acaba de tomar en la aplicacion
exports.handler = async (event) => {
    try {
        const { sourceImage, targetImage } = event || {};

        if (!sourceImage || !targetImage) {
            return buildResponse(400, 'Se requieren las dos fotos para comparar');
        }

        const resultado = await compareFaces(sourceImage, targetImage);
        console.log('Resultado:', resultado);
        return buildResponse(200, resultado);
    } catch (error) {
        console.log('Error:', error);

        // Rekognition responde InvalidParameterException cuando no encuentra un rostro
        if (error.name === 'InvalidParameterException') {
            return buildResponse(400, 'No se detecto un rostro en la foto');
        }

        return buildResponse(500, 'Error al comparar los rostros');
    }
};
