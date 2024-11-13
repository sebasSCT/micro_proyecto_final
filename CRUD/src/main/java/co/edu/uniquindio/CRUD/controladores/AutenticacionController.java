package co.edu.uniquindio.CRUD.controladores;

import co.edu.uniquindio.CRUD.dtos.general.MensajeDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.RegistroUsuarioDTO;
import co.edu.uniquindio.CRUD.excepciones.*;
import co.edu.uniquindio.CRUD.servicios.interfaces.UsuarioService;
import co.edu.uniquindio.CRUD.utils.JWTUtils;
import co.edu.uniquindio.rabbitmq.RabbitMQSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/usuarios")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Controlador para operaciones de autenticacion y de registro")
public class AutenticacionController {

    private final UsuarioService usuarioService;
    private final JWTUtils jwtUtils;
    private final RabbitMQSender rabbitMQSender;

    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o incompletos",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "409", description = "El usuario ya existe",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @PostMapping
    public ResponseEntity<MensajeDTO<String>> registrarse(
            @Valid @RequestBody RegistroUsuarioDTO usuarioDTO) throws Exception {
        try {
            usuarioService.registrarUsuario(usuarioDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(new MensajeDTO<>(false, "Usuario registrado correctamente"));
        } catch (DatosIncompletosException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (UsuarioExisteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }
    }
}
