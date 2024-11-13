Feature: Funcionamiento de la integración de todo su sistema como un conjunto único.

    Scenario: Se desea registrar un usuario y comprobar el funcionamiento de los servicios
    Given usuario con sus datos, correo "usuariodeprueba@gmail.com" contraseña "123", nombre "usuario" y apellido "de prueba"
    When Invoco el servicio que permite el registro de nuevos usuarios
    Then El usuario es registrado correctamente
    And Se genera un registro en loki
    
    @final
    Scenario: Se quiere verificar el funcionamiento del sistema de monitoreo
    Given El contenedor "observabilidad-app-crud-1"
    When Se detiene la ejecucion del contenedor
    Then Se consulta el estado del contenedor
    And Se obtiene el estado del contenedor en 0