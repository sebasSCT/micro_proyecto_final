package co.edu.uniquindio.CRUD.servicios.implementaciones;

import co.edu.uniquindio.CRUD.documentos.Usuario;
import co.edu.uniquindio.CRUD.dtos.usuario.ActualizarUsuarioDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.DetalleUsuarioDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.RegistroUsuarioDTO;
import co.edu.uniquindio.CRUD.excepciones.*;
import co.edu.uniquindio.CRUD.repositorios.UsuarioRepository;
import co.edu.uniquindio.CRUD.servicios.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public String registrarUsuario(RegistroUsuarioDTO usuario) throws Exception {

        if(usuario.nombre().isEmpty() ||
        usuario.apellido().isEmpty() || usuario.idUsuario().isEmpty()){
            throw new DatosIncompletosException("Datos de registro inválidos o incompletos");
        }

        Usuario usuarioDB = new Usuario();

        usuarioDB.setNombre(usuario.nombre());
        usuarioDB.setApellido(usuario.apellido());
        usuarioDB.setIdUsuario(usuario.idUsuario());
        usuarioDB.setEstado(true);

        Usuario usuarioGuardado = usuarioRepository.save(usuarioDB);

        return usuarioGuardado.getCodigo();
    }

    @Override
    public void actualizarUsuario(String idUsuario,ActualizarUsuarioDTO usuario, String idtoken) throws Exception {

        if(!(usuario.codigo().equals(idtoken)) || !(idUsuario.equals(idtoken)) ){
            throw new NoAutorizadoException("No puedes realizar ésta operación");
        }

        if(usuario.nombre().isEmpty() ||
                usuario.apellido().isEmpty()){
            throw new DatosIncompletosException("Datos de registro inválidos o incompletos");
        }

        Optional<Usuario> usuarioDB = usuarioRepository.findById(usuario.codigo());

        if (usuarioDB.isEmpty()) {
            throw new UsuarioNoEncontradoException("El usuario no existe");
        }

        if (!usuarioDB.get().isEstado()) {
            throw new UsuarioInactivoException("El usuario está inactivo");
        }

        Usuario usuarioActual = usuarioDB.get();
        usuarioActual.setNombre(usuario.nombre());
        usuarioActual.setApellido(usuario.apellido());
        usuarioActual.setEstado(true);

        usuarioRepository.save(usuarioActual);
    }

    @Override
    public DetalleUsuarioDTO obtenerUsuario(String idCuenta, String idtoken) throws Exception {

        if(!(idCuenta.equals(idtoken))){
            throw new Exception("No puedes realizar ésta operación, porque no eres tú");
        }

        Optional<Usuario> usuarioDB = usuarioRepository.findById(idCuenta);

        if (usuarioDB.isEmpty()) {
            throw new Exception("El usuario no existe");
        }

        Usuario usuarioActual = usuarioDB.get();

        return new DetalleUsuarioDTO(usuarioActual.getCodigo(), usuarioActual.getNombre(), usuarioActual.getApellido());
    }

    @Override
    public void eliminarUsuario(String idCuenta, String idtoken) throws Exception {


        // codigo 1234 email algoqmail.com password 1234
        // codigo 323  nombre alejo apellido salgado idUsuario 1234

        if(!(idCuenta.equals(idtoken))){
            throw new NoAutorizadoException("No puedes realizar ésta operación");
        }

        Optional<Usuario> usuarioDB = usuarioRepository.findById(idCuenta);

        if (usuarioDB.isEmpty()) {
            throw new UsuarioNoEncontradoException("El usuario no existe");
        }

        if(!usuarioDB.get().isEstado()){
            throw new UsuarioInactivoException("El usuario está inactivo");
        }

        Usuario usuarioActual = usuarioDB.get();

        usuarioActual.setEstado(false);

        Usuario usuarioGuardado = usuarioRepository.save(usuarioActual);
    }
}
