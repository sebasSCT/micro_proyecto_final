Feature: Inicio de sesión

  Un usuario registrado desea iniciar sesion con sus credenciales

  Scenario: Yo como usuario registrado quiero poder iniciar sesion
    Given Yo usuario registrado inicio sesión con mis credenciales usuario "hola@gmail" y contraseña "123"
    When Invoco el sercivicio para inicio de sesion
    Then Inicio sesion correctamente
    And El esquema de la respuesta es correcto
    
  Scenario: Yo como usuario deseo recibir notificacion sobre un inicio fallido de sesion
    Given Yo usuario registrado inicio sesión con mis credenciales usuario "hola@gmail" y contraseña "1234"
    When Invoco el sercivicio para inicio de sesion
    Then No puedo iniciar sesion
    And El esquema de la respuesta es correcto