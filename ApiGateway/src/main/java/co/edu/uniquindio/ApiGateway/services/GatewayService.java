package co.edu.uniquindio.ApiGateway.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class GatewayService {

    private final RestTemplate restTemplate = new RestTemplate();

    // URL de los microservicios
    @Value("${auth.service.url}")
    private String authServiceUrl;

    @Value("${crud.service.url}")
    private String crudServiceUrl;

    @Value("${profile.service.url}")
    private String profileServiceUrl;

    public ResponseEntity<?> procesarRegistro(Map<String, Object> payload) {
        Map<String, Object> authPayload = Map.of(
                "email", payload.get("email"),
                "password", payload.get("password")
        );

        ResponseEntity<?> authResponse = restTemplate.postForEntity(
                authServiceUrl + "/api/auth/usuarios", new HttpEntity<>(authPayload), Object.class
        );

        if (authResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println(authResponse.getBody());

            String idUsuario = "";

            Object responseBody = authResponse.getBody();
            if (responseBody instanceof LinkedHashMap) {
                LinkedHashMap<?, ?> responseMap = (LinkedHashMap<?, ?>) responseBody;
                idUsuario = (String) responseMap.get("respuesta");
                System.out.println("ID Usuario: " + idUsuario);
            }

            Map<String, Object> crudPayload = Map.of(
                    "nombre", payload.get("nombre"),
                    "apellido", payload.get("apellido"),
                    "idUsuario", idUsuario
            );

            ResponseEntity<?> crudResponse = restTemplate.postForEntity(
                    crudServiceUrl + "/api/auth/usuarios", new HttpEntity<>(crudPayload), Object.class
            );

            // Paso 3: Registrar en el servicio de perfil (8079) con los demás datos
            Map<String, Object> profilePayload = Map.ofEntries(
                    Map.entry("user_id", idUsuario),
                    Map.entry("nickname", payload.get("nickname")),
                    Map.entry("personal_url", payload.get("personal_url")),
                    Map.entry("contact_public", payload.get("contact_public")),
                    Map.entry("address", payload.get("address")),
                    Map.entry("bio", payload.get("bio")),
                    Map.entry("organization", payload.get("organization")),
                    Map.entry("country", payload.get("country")),
                    Map.entry("social_links", payload.get("social_links")),
                    Map.entry("identificacion", idUsuario),
                    Map.entry("estado", true)
            );

            ResponseEntity<?> profileResponse = restTemplate.postForEntity(
                    profileServiceUrl + "/usuario", new HttpEntity<>(profilePayload), Object.class
            );

            if (crudResponse.getStatusCode().is2xxSuccessful() && profileResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("Usuario registrado con éxito");
            } else {
                return ResponseEntity.status(500).body("Error al registrar el usuario en CRUD o perfil");
            }
        } else {
            return ResponseEntity.status(400).body("No se pudo registrar en el servicio de autenticación");
        }
    }

    // Login de usuario
    public ResponseEntity<?> login(Map<String, Object> payload) {
        ResponseEntity<?> authResponse = restTemplate.postForEntity(
                authServiceUrl + "/api/auth/usuarios/login", new HttpEntity<>(payload), Object.class
        );

        if (authResponse.getStatusCode().is2xxSuccessful()) {
            return authResponse;
        } else {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    // Obtener datos del usuario
    public ResponseEntity<?> obtenerDatosUsuario(String codigo, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<?> authResponse = restTemplate.exchange(
                authServiceUrl + "/api/usuarios/" + codigo,
                HttpMethod.GET,
                entity,
                Object.class
        );


        ResponseEntity<?> crudResponse = restTemplate.exchange(
                crudServiceUrl + "/api/usuarios/" + codigo,
                HttpMethod.GET,
                entity,
                Object.class
        );

        ResponseEntity<?> profileResponse = restTemplate.getForEntity(
                profileServiceUrl + "/usuario/" + codigo, Object.class
        );

        // Combinar las respuestas y devolver una respuesta combinada
        if (authResponse.getStatusCode().is2xxSuccessful() &&
                crudResponse.getStatusCode().is2xxSuccessful() &&
                profileResponse.getStatusCode().is2xxSuccessful()) {
            Map<String, Object> combinedResponse = Map.of(
                    "authData", authResponse.getBody(),
                    "crudData", crudResponse.getBody(),
                    "profileData", profileResponse.getBody()
            );
            return ResponseEntity.ok(combinedResponse);
        } else {
            return ResponseEntity.status(500).body("Error al obtener los datos del usuario");
        }
    }

    // Actualizar datos del usuario
    public ResponseEntity<?> actualizarDatosUsuario(String codigo, Map<String, Object> payload, String token) {

        Map<String, Object> authPayload = Map.of(
                "codigo", codigo,
                "email", payload.get("email")
        );

        // Llamar a los microservicios para actualizar
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Actualizar en el servicio de autenticación
        ResponseEntity<?> authResponse = restTemplate.exchange(
                authServiceUrl + "/api/usuarios/" + codigo, HttpMethod.PUT,
                new HttpEntity<>(authPayload, headers), Object.class
        );

        Map<String, Object> crudPayload = Map.of(
                "codigo", codigo,
                "nombre", payload.get("nombre"),
                "apellido", payload.get("apellido")
        );

        // Actualizar en el servicio CRUD
        ResponseEntity<?> crudResponse = restTemplate.exchange(
                crudServiceUrl + "/api/usuarios/" + codigo, HttpMethod.PUT,
                new HttpEntity<>(crudPayload, headers), Object.class
        );

        Map<String, Object> profilePayload = Map.ofEntries(
                Map.entry("nickname", payload.get("nickname")),
                Map.entry("personal_url", payload.get("personal_url")),
                Map.entry("contact_public", payload.get("contact_public")),
                Map.entry("address", payload.get("address")),
                Map.entry("bio", payload.get("bio")),
                Map.entry("organization", payload.get("organization")),
                Map.entry("country", payload.get("country")),
                Map.entry("social_links", payload.get("social_links")),
                Map.entry("estado", true)
        );

        // Actualizar en el servicio de perfil
        ResponseEntity<?> profileResponse = restTemplate.exchange(
                profileServiceUrl + "/usuario/" + codigo, HttpMethod.PUT,
                new HttpEntity<>(profilePayload, headers), Object.class
        );

        if (authResponse.getStatusCode().is2xxSuccessful() &&
                crudResponse.getStatusCode().is2xxSuccessful() &&
                profileResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok("Datos actualizados con éxito");
        } else {
            return ResponseEntity.status(500).body("Error al actualizar los datos");
        }
    }

    // Eliminar usuario
    public ResponseEntity<?> eliminarUsuario(String codigo, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Eliminar en los tres microservicios
        ResponseEntity<?> authResponse = restTemplate.exchange(
                authServiceUrl + "/api/usuarios/" + codigo,
                HttpMethod.DELETE,
                entity,
                Object.class
        );

        ResponseEntity<?> crudResponse = restTemplate.exchange(
                crudServiceUrl + "/api/usuarios/" + codigo,
                HttpMethod.DELETE,
                entity,
                Object.class
        );

        ResponseEntity<?> profileResponse = restTemplate.exchange(
                profileServiceUrl + "/usuario/" + codigo,
                HttpMethod.DELETE,
                entity,
                Object.class
        );

        if (authResponse.getStatusCode().is2xxSuccessful() &&
                crudResponse.getStatusCode().is2xxSuccessful() &&
                profileResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok("Usuario eliminado con éxito");
        } else {
            return ResponseEntity.status(500).body("Error al eliminar el usuario");
        }
    }
}
