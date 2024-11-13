Feature: Funcionamiento del servicio de monitoreo

    Scenario: Yo quiero consultar de los servicios que estan activos
    Given Estado "up" en "1"
    When Invoco el servicio para realizar la consulta
    Then Obtengo los servicios en el estado esperado

    Scenario: Yo quiero consultar los servicios que no se encuentran activos
    Given Estado "up" en "0"
    When Invoco el servicio para realizar la consulta
    Then Obtengo los servicios en el estado esperado

    Scenario: Yo quiero consultar el estado de un servicio
    Given Servicio con el nombre "app-crud" y en su instancia "8084"
    When Invoco el servicio para realizar la consulta
    Then Obtengo informacion sobre el el servicio

    

    