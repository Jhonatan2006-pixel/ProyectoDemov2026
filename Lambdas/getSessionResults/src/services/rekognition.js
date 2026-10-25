const { RekognitionClient, GetFaceLivenessSessionResultsCommand } = require('@aws-sdk/client-rekognition');

// Face Liveness solo existe en algunas regiones, como us-east-1
const rekognitionClient = new RekognitionClient({ region: 'us-east-1' });

const getLivenessResults = async (sessionId) => {
    const command = new GetFaceLivenessSessionResultsCommand({ SessionId: sessionId });
    const response = await rekognitionClient.send(command);

    // ReferenceImage es el cuadro del video que se usa para comparar
    // el rostro contra la foto de perfil (Lambda compareFaces)
    const referencia = response.ReferenceImage && response.ReferenceImage.Bytes;

    return {
        status: response.Status,
        confidence: response.Confidence,
        referenceImage: referencia ? Buffer.from(referencia).toString('base64') : null
    };
};

module.exports = { getLivenessResults };
