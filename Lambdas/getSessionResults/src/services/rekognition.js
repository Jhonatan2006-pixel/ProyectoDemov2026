const { RekognitionClient, GetFaceLivenessSessionResultsCommand } = require('@aws-sdk/client-rekognition');

const rekognitionClient = new RekognitionClient({ region: 'us-east-1' });

const getLivenessResults = async (sessionId) => {
    const command = new GetFaceLivenessSessionResultsCommand({
        SessionId: sessionId,
    });

    const response = await rekognitionClient.send(command);

    const confidence = response.Confidence ?? 'No se encontró nivel de confianza';
    const status = response.Status ?? 'No se encontró un estado';
    const referenceImageBytes = response.ReferenceImage?.Bytes ?? null;

    let referenceImageBase64 = 'No se encontró imagen de referencia';
    if (referenceImageBytes !== null) {
        referenceImageBase64 = Buffer.from(referenceImageBytes).toString('base64');
    }

    return { confidence, status, referenceImageBase64 };
};

module.exports = { getLivenessResults };
