package co.edu.uniquindio.talleruno.excepciones;

public class CredencialesInvalidasException extends RuntimeException{
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
