import { InvokeCommand, LambdaClient } from "@aws-sdk/client-lambda";

// Nombres de las Lambdas en AWS. Se pueden cambiar en el archivo .env
export const LAMBDAS = {
    compareFaces: () => process.env.LAMBDA_COMPARE_FACES || "compare-faces",
    getSessionId: () => process.env.LAMBDA_GET_SESSION_ID || "liveness-get-session-id",
    getSessionResults: () =>
        process.env.LAMBDA_GET_SESSION_RESULTS || "liveness-get-session-results"
};

// Respuesta de una Lambda: el mismo formato que arma buildResponse
export interface RespuestaLambda {
    statusCode: number;
    body: any;
}

let cliente: LambdaClient | null = null;

// El cliente se crea al usarlo por primera vez, cuando el .env ya está cargado.
// Las credenciales (Client Credentials) se leen de AWS_ACCESS_KEY_ID y
// AWS_SECRET_ACCESS_KEY del .env del servidor.
const obtenerCliente = (): LambdaClient => {
    if (!cliente) {
        cliente = new LambdaClient({
            region: process.env.AWS_REGION || "us-east-2"
        });
    }

    return cliente;
};

// Ejecuta una Lambda y devuelve su respuesta. Lanza un error si AWS
// no deja ejecutarla o si la Lambda falla sin responder.
export const invocarLambda = async (
    nombre: string,
    datos: object
): Promise<RespuestaLambda> => {

    const respuesta = await obtenerCliente().send(new InvokeCommand({
        FunctionName: nombre,
        Payload: Buffer.from(JSON.stringify(datos))
    }));

    const texto = Buffer.from(respuesta.Payload ?? []).toString("utf8");

    if (respuesta.FunctionError) {
        throw new Error(`La Lambda ${nombre} falló: ${texto}`);
    }

    const resultado = JSON.parse(texto);

    return {
        statusCode: resultado.statusCode,
        body: typeof resultado.body === "string"
            ? JSON.parse(resultado.body)
            : resultado.body
    };
};
