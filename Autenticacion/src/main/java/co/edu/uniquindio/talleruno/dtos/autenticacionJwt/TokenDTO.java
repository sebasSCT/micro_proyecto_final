package co.edu.uniquindio.talleruno.dtos.autenticacionJwt;

import jakarta.validation.constraints.NotBlank;
public record TokenDTO (
        @NotBlank
        String token
){
}