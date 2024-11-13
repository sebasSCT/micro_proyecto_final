Feature: Registro

  Un usuario no registrado desea hacer un registro con sus datos

  Scenario: Yo como usuario no registrado quiero poder registrarme con mis datos
    Given Yo usuario no registrado envio mis datos correo "registrar@gmail", contraseña "1234", nombre "registrar" y apellido "registrar"
    When Invoco el servicio que permite el registro de nuevos usuarios con un usuario no existente
    Then Me registro correctamente
    And El esquema de la respuesta es correcto

  Scenario: Yo como usuario no registrado deseo recibir notificacion si realizo mal el registro
    Given Yo usuario no registrado envio mis datos correo "hola@gmail", contraseña "123", nombre "hola" y apellido "perez"
    When Invoco el servicio que permite el registro de nuevos usuarios con un usuario existente
    Then No puedo registrarme
    And El esquema de la respuesta es correcto

  Scenario: Registrar un usuario con datos aleatorios
    Given Datos aleatorios para la creacion de un usuario
    When Invoco el servicio que permite el registro de nuevos usuarios con un usuario no existente
    Then Me registro correctamente
    And El esquema de la respuesta es correcto
