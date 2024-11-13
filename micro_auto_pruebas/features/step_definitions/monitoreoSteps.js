const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');
const url = process.env.PROMETHEUS_URL;

let monitoreoRequest = {};
let monitoreoResponse = {};

// Scenario: Yo quiero consultar de los servicios que estan activos

Given('Estado {string} en {string}', 
    function (estado, boolean) {
    monitoreoRequest = `${estado}==${boolean}`;
});

When('Invoco el servicio para realizar la consulta', async function(){
    try {
        monitoreoResponse = (await axios.post(`${url}/api/v1/query?query=${monitoreoRequest}`)).data;
    } catch (error) {
        monitoreoResponse = error.response.data;
    };
});

Then ('Obtengo los servicios en el estado esperado', function (){
    // console.log(monitoreoResponse);
    assert.equal(monitoreoResponse.status, 'success');
});

// Scenario: Yo quiero consultar el estado de un servicio

Given('Servicio con el nombre {string} y en su instancia {string}', 
    function (servicio, instancia) {
    monitoreoRequest = `up{instance="${servicio}:${instancia}", job="${servicio}"}`;
});

Then ('Obtengo informacion sobre el el servicio', function (){
    // console.log(monitoreoResponse);
    assert.equal(monitoreoResponse.status, 'success');
});