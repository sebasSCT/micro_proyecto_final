package co.edu.uniquindio.ApiGateway.dtos.general;

public record MensajeDTO<T>(
        boolean error,
        T respuesta
) {
}