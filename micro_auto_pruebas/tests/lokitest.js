const axios = require('axios');


let logsResponse = {};

async function logs (){

    try {
        // Definir la URL base de Loki (ajústala según tu configuración)
        const lokiUrl = 'http://localhost:3100/loki/api/v1/query_range';
    
        // Configurar los parámetros de la consulta
        const params = {
        query: '{job="docker", filename="/var/lib/docker/containers/23e7ee209db37763a1e7b650d1f4ebb41d35c29607eac90c6fa7cf3336666999/23e7ee209db37763a1e7b650d1f4ebb41d35c29607eac90c6fa7cf3336666999-json.log"}', // La consulta en Loki usando el formato LogQL
        start: 1727758800,
        end: 1730350800,
        limit: 100, // Limitar los resultados
        };
    
        // Hacer la petición GET a Loki
        try{
            console.log({params});
            const response = await axios.get(lokiUrl, {params});
            // Manejar la respuesta y mostrar los logs
            const logs = response.data.data.result;
            logs.forEach((logStream) => {
            logStream.values.forEach((log) => {
                const timestamp = log[0];
                const message = log[1];
                // console.log(`Timestamp: ${timestamp}, Log: ${message}`);
                logsResponse += `Timestamp: ${timestamp}, Log: ${message}\n`;
            });
            });
        } catch (error) {}
        
    
        
        
    } catch (error) {
        response = error.response;
    }
    
    console.log(logsResponse)
    
}

(async () => {
    await logs();
})();