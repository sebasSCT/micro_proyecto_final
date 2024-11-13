package co.edu.uniquindio.talleruno.excepciones;

public class UsuarioExisteException extends RuntimeException {
    public UsuarioExisteException(String message) {
        super(message);
    }
}