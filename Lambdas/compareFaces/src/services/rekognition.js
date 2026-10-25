const { RekognitionClient, CompareFacesCommand } = require('@aws-sdk/client-rekognition');

// CompareFaces si esta disponible en US East (Ohio), la region de la cuenta
const rekognitionClient = new RekognitionClient({ region: 'us-east-2' });

// Porcentaje minimo de parecido para considerar que es la misma persona
const SIMILITUD_MINIMA = 85;

const compareFaces = async (sourceImage, targetImage) => {
    const command = new CompareFacesCommand({
        SourceImage: { Bytes: Buffer.from(sourceImage, 'base64') },
        TargetImage: { Bytes: Buffer.from(targetImage, 'base64') },
        SimilarityThreshold: SIMILITUD_MINIMA
    });
    const response = await rekognitionClient.send(command);

    // FaceMatches solo trae los rostros que superaron la similitud minima
    const mejor = (response.FaceMatches || [])[0];

    return {
        coincide: !!mejor,
        similitud: mejor ? Number(mejor.Similarity.toFixed(2)) : 0
    };
};

module.exports = { compareFaces };
