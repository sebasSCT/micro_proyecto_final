package co.edu.uniquindio.ApiGateway.utils;

import co.edu.uniquindio.ApiGateway.dtos.general.MensajeDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class FiltroToken implements WebFilter {
    private final JWTUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Configuración de cabeceras para CORS
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, PATCH, OPTIONS");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, Authorization");
        exchange.getResponse().getHeaders().add("Access-Control-Allow-Credentials", "true");

        if ("OPTIONS".equals(exchange.getRequest().getMethod().name())) {
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }

        String requestURI = exchange.getRequest().getPath().toString();
        String token = getToken(exchange);

        if (requestURI.startsWith("/api/usuarios") && token == null) {
            return crearRespuestaError("No hay un Token", HttpStatus.FORBIDDEN, exchange);
        }

        try {
            if (requestURI.startsWith("/api/usuarios") && token != null) {
                // Aquí podrías añadir lógica para validar el token si es necesario
            }
            return chain.filter(exchange); // Continuar con el filtro si no hay errores
        } catch (MalformedJwtException | SignatureException e) {
            return crearRespuestaError("El token es incorrecto", HttpStatus.INTERNAL_SERVER_ERROR, exchange);
        } catch (ExpiredJwtException e) {
            return crearRespuestaError("El token está vencido", HttpStatus.INTERNAL_SERVER_ERROR, exchange);
        } catch (Exception e) {
            return crearRespuestaError(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, exchange);
        }
    }

    private String getToken(ServerWebExchange exchange) {
        String header = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.replace("Bearer ", "");
        }
        return null;
    }

    private Mono<Void> crearRespuestaError(String mensaje, HttpStatus status, ServerWebExchange exchange) {
        MensajeDTO<String> dto = new MensajeDTO<>(true, mensaje);
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes;
        try {
            bytes = new ObjectMapper().writeValueAsBytes(dto);
        } catch (JsonProcessingException e) {
            bytes = ("{\"error\":\"Error de procesamiento JSON\"}").getBytes(StandardCharsets.UTF_8);
        }

        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(bytes)));
    }
}
