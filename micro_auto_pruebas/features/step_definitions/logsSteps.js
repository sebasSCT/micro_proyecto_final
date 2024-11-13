const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');
require('dotenv').config();
const url = `${process.env.LOKI_URL}/loki/api/v1/query_range`;
const Docker = require('dockerode');
const docker = new Docker();

let params = {};
let logResponse = {};
let logs = null;

//Scenario: Yo quiero consultar todos los logs que se encuentran en el sistema

Given('Parametros validos para la consulta de logs', function () {
    params = {
        query: '{job="docker"}',
        start: 1727758800,
        end: 1730350800,
        limit: 100,
    };
    });

When('Invoco el servicio para consultar los logs', async function (){
    logs = "";
    try {
         logResponse = (await axios.get(url, {params})).data.data.result;

         logResponse.forEach((logStream) => {
            logStream.values.forEach((log) => {
                const timestamp = log[0];
                const message = log[1];
                logs += `Timestamp: ${timestamp}, Log: ${message}\n`;
            });
            });

    } catch (error) {
        logResponse = error.response.data;
    }

});

Then ('Obtengo todos los logs del sistema', function (){
    // console.log(logs);
    assert.notEqual(logs, null);
});

//Scenario: Yo quiero consultar los logs relacionados con el contenedor de loki

Given('Parametros validos con el nombre completo del contenedor {string}', async function (full_container_name) {
    const containers = await docker.listContainers();


    const container = containers.find((cont) => {
        return cont.Names.some((name) => name === `/${full_container_name}`);
    });

    if (!container) {
        throw new Error(`No se encontró el contenedor con nombre: ${full_container_name}`);
    }

    const containerId = container.Id;

    params = {
        query: `{job="docker", filename="/var/lib/docker/containers/${containerId}/${containerId}-json.log"}`,
        start: 1727758800,  // Ajusta estos valores según tu necesidad
        end: 1730350800,
        limit: 100,
    };
});

// Scenario: Yo quiero consultar los logs relacionados con el contenedor de app-crud

Then('Obtengo los logs relacionados con el contenedor app-crud', function (){
    // console.log(logs)
    assert.equal(logs.includes("CRUD"), true);
});

// Scenario: Yo quiero consultar los logs de un dia especifico

let unixTimestamp;
let unixTimestamp_end;

Given ('Parametros validos con la fecha del día {string}', function (fecha_){

    const fecha = `${fecha_} 00:00:00`;
    const fecha_end = `${fecha_} 23:59:59`;
    const date = new Date(fecha);
    const date_end = new Date(fecha_end);
    unixTimestamp = Math.floor(date.getTime() / 1000);
    unixTimestamp_end = Math.floor(date_end.getTime() / 1000);

    params = {
        query: `{job="docker"}`,
        start: unixTimestamp,
        end: unixTimestamp_end,
        limit: 1,
    };
});

Then('Obtengo los logs en el rango esperado', function (){
    const logLines = logs.split('\n');
    let allLogsInRange = true;

    for (const line of logLines) {
        if (line.trim() === '') continue;

        const logParts = line.split('Log: ');
        if (logParts.length < 2) continue;

        let logObject;
        try {
            logObject = JSON.parse(logParts[1]);
        } catch (e) {
            console.error('Error parseando log JSON:', e);
            console.error('Problema en la linea log :', line);
            continue;
        }

        if (!logObject.log) {
            console.warn('El objeto Log no contiene la propiedad "log":', logObject);
            continue;
        }

        const tsMatch = logObject.log.match(/ts=(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\.\d+Z)/);
        if (!tsMatch) {
            console.warn('No se puede encontrar el timestamp en el log:', logObject.log);
            continue;
        }

        const timestamp = new Date(tsMatch[1]).getTime() / 1000; // Convert to Unix timestamp

        if (timestamp < unixTimestamp || timestamp > unixTimestamp_end) {
            allLogsInRange = false;
            break;
        }
    }

    assert.equal(allLogsInRange, true, '\n' + 'Algunos registros están fuera del rango de tiempo esperado');
});
