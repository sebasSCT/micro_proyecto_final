package co.edu.uniquindio.talleruno.excepciones;

public class UsuarioNoEncontradoException extends RuntimeException{
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}