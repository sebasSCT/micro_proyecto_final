package co.edu.uniquindio.talleruno.dtos.general;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}