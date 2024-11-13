package co.edu.uniquindio.ApiGateway.controllers;

import co.edu.uniquindio.ApiGateway.services.GatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/gateway/usuario")
public class GatewayController {

    @Autowired
    private GatewayService gatewayService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> payload) {
        return gatewayService.login(payload);
    }

    @PostMapping
    public ResponseEntity<?> registrarUsuario(@RequestBody Map<String, Object> payload) {
        return gatewayService.procesarRegistro(payload);
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<?> obtenerDatosUsuario(@PathVariable String codigo, ServerHttpRequest request) {

        String token = getToken(request);
        System.out.println(token);
        return gatewayService.obtenerDatosUsuario(codigo, token);
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<?> actualizarDatosUsuario(
            @PathVariable String codigo,
            @RequestBody Map<String, Object> payload,
            ServerHttpRequest request) {

        String token = getToken(request);
        return gatewayService.actualizarDatosUsuario(codigo, payload, token);
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String codigo, ServerHttpRequest request) {

        String token = getToken(request);

        return gatewayService.eliminarUsuario(codigo, token);

    }

    private String getToken(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
