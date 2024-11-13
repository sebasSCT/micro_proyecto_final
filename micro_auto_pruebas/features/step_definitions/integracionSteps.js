const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');
const decode = require('./../../decode');
require('dotenv').config();
const Docker = require('dockerode');
const docker = new Docker();

const url_crud = process.env.BASE_URL;
const url_loki = `${process.env.LOKI_URL}/loki/api/v1/query_range`;
const url_prom = process.env.PROMETHEUS_URL;
const crud_docker = process.env.CONT_CRUD;
const crud_name = "app-crud";
const instancia = '8084';


let loginRequest = {};
let loginResponse = {};
let signRequest = {};
let signResponse = {};
let logsRequest = {};
let logsResponse = {};
let deleteRequest = {};
let deleteResponse = {};
let monitoreoRequest = {};
let monitoreoResponse = {};
let logs = {};
let containerName = {};
let containerId = {};

// Scenario: Se desea registrar un usuario y comprobar el funcionamiento de los servicios

Given('usuario con sus datos, correo {string} contraseña {string}, nombre {string} y apellido {string}', 
    function (email, password, name, lastname) {
        let numeroAleatorio = Math.floor(Math.random() * (9999 - 1 + 1)) + 1;
        email = email + numeroAleatorio;
    signRequest = {
        email: email,
        password: password,
        nombre: name,
        apellido: lastname 
    }
    loginRequest = {
        email: email,
        password: password
    };
});

When('Invoco el servicio que permite el registro de nuevos usuarios', async function(){
    try {
        signResponse = (await axios.post(`${url_crud}/api/auth/usuarios`, signRequest)).data;

        const containers = await docker.listContainers();
        const container = containers.find((cont) => {
            return cont.Names.some((name) => name === `/${crud_docker}`);
        });
        let containerId = container.Id;
        params = {
            query: `{job="docker", filename="/var/lib/docker/containers/${containerId}/${containerId}-json.log"}`,
            start: 1727758800,  // Ajusta estos valores según tu necesidad
            end: 1730350800,
            limit: 100,
        }
        logsResponse = (await axios.get(url_loki, {params})).data.data.result;
         logsResponse.forEach((logStream) => {
            logStream.values.forEach((log) => {
                const timestamp = log[0];
                const message = log[1];
                logs += `Timestamp: ${timestamp}, Log: ${message}\n`;
            });
            });
        
        loginResponse = (await axios.post(`${url_crud}/api/auth/usuarios/login`, loginRequest)).data;
        usercode = decode.decodetoken(loginResponse.respuesta.token);
        deleteRequest = {
            headers: {
                Authorization: `Bearer ${loginResponse.respuesta.token}`
            }
        }
        deleteResponse = (await axios.delete(`${url_crud}/api/usuarios/${userCode}`, deleteRequest)).data;

    } catch (error) {
        
    };
});

Then ('El usuario es registrado correctamente', function (){
    // console.log(loginResponse);
    assert.strictEqual(loginResponse.error, false);
});

Then ('Se genera un registro en loki', function (){
    // console.log(monitoreoResponse);
    assert.notEqual(logs, null);
});

// Scenario: Se quiere verificar el funcionamiento del sistema de monitoreo

Given ('El contenedor {string}', function(container){
    containerName = container;
});

When ('Se detiene la ejecucion del contenedor', async function(){
    const containers = await docker.listContainers();
    const container = containers.find((cont) => {
        return cont.Names.some((name) => name === `/${containerName}`);
    });
    containerId = container.Id;
    try {
        const container = docker.getContainer(containerId); // Obtén el contenedor por ID o nombre
        await container.stop(); // Detén el contenedor
        await esperar(2000);
    } catch (error) {
        console.error(`Error al detener el contenedor ${containerId}:`, error);
    }
});

Then ('Se consulta el estado del contenedor', async function(){
    
    monitoreoRequest = `up{instance="${crud_name}:${instancia}", job="${crud_name}"}`;
    try {
        monitoreoResponse = (await axios.post(`${url_prom}/api/v1/query?query=${monitoreoRequest}`)).data;
    } catch (error) {
        
    };
    assert.equal(monitoreoResponse.status, 'success');
});

Then ('Se obtiene el estado del contenedor en 0', async function(){
    await esperar(2000);

    assert.equal(monitoreoResponse.data.result[0].value[1], '0');

    try {
        const container = docker.getContainer(containerId); // Obtén el contenedor por ID o nombre
        await container.start(); // Inicia el contenedor
    } catch (error) {
        console.error(`Error al iniciar el contenedor ${containerId}:`, error);
    }
});

function esperar(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
