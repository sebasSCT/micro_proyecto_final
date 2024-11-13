package co.edu.uniquindio.talleruno.dtos.usuario;

import jakarta.validation.constraints.NotBlank;

public record ActualizarUsuarioDTO(
        @NotBlank
        String codigo,
        @NotBlank
        String email
) {
}
