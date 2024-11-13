Feature: Eliminar usuario

    Un usuario registrado y con sesion activa desea eliminar su usuario

    Scenario: Yo como usuario deseo poder eliminar mi cuenta
        Given Soy usuario con las credenciales, correo "borrar@gmail" y contraseña "1234"
        When Invoco el servicio para eliminar el usuario
        Then Elimino el perfil de usuario correctamente