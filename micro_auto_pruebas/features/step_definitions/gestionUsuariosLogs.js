const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');
require('dotenv').config();
const Docker = require('dockerode');
const docker = new Docker();

const url = 'http://localhost:3100/loki/api/v1/query_range';
const urlCrud = process.env.BASE_URL;
const cont_crud = process.env.CONT_CRUD;

let loginRequest = {};
let loginResponse = {};
let logEntries = [];

Given('existe un usuario con credenciales válidas con el nombre de usuario {string} y la contraseña {string}', async function (email, password) {
    loginRequest = {
        email: email,
        password: password
    };
});

// When: se invoca el servicio de inicio de sesion
When('se invoca el servicio de inicio de sesion', async function () {
    try {
        loginResponse = (await axios.post(`${urlCrud}/api/auth/usuarios/login`, loginRequest)).data;
    } catch (error) {
        loginResponse = error.response.data;
    }
});

// Then: Inicio de sesión correcto
Then('Inicio de sesion correcto', function () {
    assert.strictEqual(loginResponse.error, false);
});

// And: Se registra un log de inicio de sesión en el sistema de logs
Then('se registra un log de inicio de sesión en el sistema de logs', async function () {
    const containers = await docker.listContainers();

    const container = containers.find((cont) => {
        return cont.Names.some((name) => name === `/${cont_crud}`);
    });

    if (!container) {
        throw new Error(`No se encontró el contenedor con nombre: ${cont_crud}`);
    }

    const containerId = container.Id;

    const logsParams = {
        query: `{job="docker", filename="/var/lib/docker/containers/${containerId}/${containerId}-json.log"} |= "CRUD"`,
        start: Math.floor(Date.now() / 1000) - 60,
        end: Math.floor(Date.now() / 1000),
        limit: 100,
    };

    try {
        const response = await axios.get(url, { params: logsParams });
        const logs = response.data.data.result;

        // Print the entire response
        // console.log('Logs response:', JSON.stringify(logs, null, 2));

        if (logs.length > 0) {
            const lastLog = logs[logs.length - 1];
            // console.log('Last log entry:', lastLog);

            // Check if the last log contains the email
            const logContainsEmail = lastLog.values.some(value =>
                value[1].toLowerCase().includes(loginRequest.email.toLowerCase())
            );

            // console.log(`Log contains email (${loginRequest.email}):`, logContainsEmail);

            assert.strictEqual(logContainsEmail, true, 'El último log debe contener el email del usuario que inició sesión');
        } else {
            // console.log('No se encontraron logs');
            assert.fail('No se encontraron logs');
        }
    } catch (error) {
        // console.error('Error al obtener los logs:', error);
        throw error;
    }
});