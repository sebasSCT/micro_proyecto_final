Feature: Consultar informacion de usuario

    Un usuario registrado y con sesion activa desea consultar informacion sobre su perfil

    Scenario: Yo como usuario registrado deseo poder consultar mi informacion guardada
            Given Soy un usuario registrado con credenciales, correo "hola@gmail" y contraseña "123"
            When Invoco el servicio de consulta de usuario
            Then Obtengo correctamente los datos