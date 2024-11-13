package co.edu.uniquindio.talleruno.services.implementacion;

import co.edu.uniquindio.talleruno.documentos.Usuario;
import co.edu.uniquindio.talleruno.dtos.autenticacionJwt.TokenDTO;
import co.edu.uniquindio.talleruno.dtos.general.LoginDTO;
import co.edu.uniquindio.talleruno.excepciones.CredencialesInvalidasException;
import co.edu.uniquindio.talleruno.excepciones.DatosIncompletosException;
import co.edu.uniquindio.talleruno.excepciones.UsuarioInactivoException;
import co.edu.uniquindio.talleruno.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.talleruno.repositorios.UsuarioRepository;
import co.edu.uniquindio.talleruno.services.interfaces.AutenticacionServicio;
import co.edu.uniquindio.talleruno.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutenticacionServiceImpl implements AutenticacionServicio {

    private final UsuarioRepository usuarioRepository;
    private final JWTUtils jwtUtils;

    @Override
    public TokenDTO login(LoginDTO loginDTO) throws Exception {

        if(loginDTO.password().isEmpty() || loginDTO.email().isEmpty()){
            throw new DatosIncompletosException("Datos de entrada incompletos");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Object[] datos = buscarCorreo(loginDTO);

        if (!passwordEncoder.matches(loginDTO.password(), datos[2].toString())) {
            throw new CredencialesInvalidasException("Credenciales incorrectas");
        }

        return new TokenDTO(crearToken(datos));
    }

    private String crearToken(Object[] datos) {

        Map<String, Object> map = new HashMap<>();
        map.put("id", datos[1]);

        return jwtUtils.generarToken(datos[0].toString(), map);
    }

    public Object[] buscarCorreo(LoginDTO loginDTO) throws Exception {

        String correo = "";
        String codigo = "";
        String password = "";

        Optional<Usuario> usuario = usuarioRepository.findByEmail(loginDTO.email());

        if (usuario.isEmpty()) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        }else if(usuario.get().isEstado()){

            correo = usuario.get().getEmail();
            codigo = usuario.get().getCodigo();
            password = usuario.get().getPassword();

        }else{
            throw new UsuarioInactivoException("Usuario inactivo");
        }

        return new Object[]{correo, codigo, password };
    }
}
