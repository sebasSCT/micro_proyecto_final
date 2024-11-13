package co.edu.uniquindio.talleruno.dtos.usuario;

import jakarta.validation.constraints.NotBlank;

public record RegistroUsuarioDTO (
        @NotBlank
        String email,
        @NotBlank
        String password){
}
