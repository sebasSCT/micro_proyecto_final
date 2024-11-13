package co.edu.uniquindio.talleruno.dtos.general;

import jakarta.validation.constraints.NotBlank;

public record CambioPasswordDTO (
                    @NotBlank
                    String nuevaPassword){
}
