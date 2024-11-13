Feature: Consulta de logs centralizados

    Se consultan los logs que se encuentran en el servicio de logs centralizado

    Scenario: Yo quiero consultar todos los logs que se encuentran en el sistema
        Given Parametros validos para la consulta de logs
        When Invoco el servicio para consultar los logs
        Then Obtengo todos los logs del sistema

    Scenario: Yo quiero consultar los logs relacionados con el contenedor de loki
        Given Parametros validos con el nombre completo del contenedor "observabilidad-loki-1"
        When Invoco el servicio para consultar los logs
        Then Obtengo todos los logs del sistema

    Scenario: Yo quiero consultar los logs relacionados con el contenedor de app-crud
        Given Parametros validos con el nombre completo del contenedor "observabilidad-app-crud-1"
        When Invoco el servicio para consultar los logs
        Then Obtengo todos los logs del sistema
        And Obtengo los logs relacionados con el contenedor app-crud

    Scenario: Yo quiero consultar los logs de un dia especifico
        Given Parametros validos con la fecha del día "2024-10-15"
        When Invoco el servicio para consultar los logs
        Then Obtengo todos los logs del sistema
        And Obtengo los logs en el rango esperado
        