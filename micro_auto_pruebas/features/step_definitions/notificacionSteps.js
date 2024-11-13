const { Given, When, Then } = require('@cucumber/cucumber');
const axios = require('axios');
const assert = require('assert');
const url = process.env.NOTIFICATION_URL;

let notificationRequest = {};
let notificationResponse = {};

// Scenario: Yo quiero enviar una notificacion a el servicio de notificaciones

Given('Tengo el mensaje {string} y el correo {string}', 
    function (message, email) {
    notificationRequest = {
        email: email,
        message: message
    };
});

When('Invoco el servicio para enviar notificaciones', async function(){
    try {
        notificationResponse = await axios.post(`${url}/send`, notificationRequest, {timeout:20000});
    } catch (error) {
        notificationResponse = error.response.data;
    };
});


Then ('Se confirma el envio de la notificacion', function (){
    // console.log(notificationResponse);
    assert.equal(notificationResponse.status, 200);
});

// Scenario: Yo quiero verificar todas las notificaciones guardadas

Given('Parametros validos para la consulta', 
    function () {} 
);

When('Invoco el servicio para obtener las notificaciones', async function(){
    try {
        notificationResponse = await axios.post(`${url}/notification`);
    } catch (error) {
        notificationResponse = error.response.data;
    };
});

Then ('Se recibe la lista de notificaciones', function (){
    // console.log(notificationResponse);
    assert.notEqual(notificationResponse, null);
});