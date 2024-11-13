Feature: Integración de gestión de usuarios y logs

  Scenario: Verificar registro de log tras login exitoso
    Given existe un usuario con credenciales válidas con el nombre de usuario "hola@gmail" y la contraseña "123"
    When se invoca el servicio de inicio de sesion
    Then Inicio de sesion correcto
    And se registra un log de inicio de sesión en el sistema de logs
