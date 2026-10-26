const { getLivenessResults } = require('./services/rekognition');
const { buildResponse } = require('./utils/response');

exports.handler = async (event) => {
    console.log('evento entrante', event);

    const httpMethod = event.httpMethod || event.requestContext?.http?.method;

    if (httpMethod === 'OPTIONS') {
        console.log('Manejar la solicitud OPTIONS preflight');
        return buildResponse(200, { message: 'Preflight check' });
    }

    let body;
    try {
        body = JSON.parse(event.body);
        console.log('body parseado', body);
    } catch (error) {
        console.error('Error parsing body:', error);
        return buildResponse(400, { message: 'Invalid JSON in request body' });
    }

    const result = await getLivenessResults(body.sessionId);
    console.log('resultado liveness', result);

    return buildResponse(200, result);
};
