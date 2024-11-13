Feature: Funcionamiento del microservicio de notificaciones

    Scenario: Yo quiero enviar una notificacion a el servicio de notificaciones
    Given Tengo el mensaje "notificacion de prueba" y el correo "sebastian.carmonat@uqvirtual.edu.co"
    When Invoco el servicio para enviar notificaciones
    Then Se confirma el envio de la notificacion
    
    Scenario: Yo quiero verificar todas las notificaciones guardadas
    Given Parametros validos para la consulta
    When Invoco el servicio para obtener las notificaciones
    Then Se recibe la lista de notificaciones