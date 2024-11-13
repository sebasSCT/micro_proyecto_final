package co.edu.uniquindio.talleruno.excepciones;

public class UsuarioInactivoException extends RuntimeException{
    public UsuarioInactivoException(String message) {
        super(message);
    }
}