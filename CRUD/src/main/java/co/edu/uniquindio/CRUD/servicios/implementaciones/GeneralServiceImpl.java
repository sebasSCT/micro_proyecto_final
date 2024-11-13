package co.edu.uniquindio.CRUD.servicios.implementaciones;

import co.edu.uniquindio.CRUD.documentos.Usuario;
import co.edu.uniquindio.CRUD.dtos.usuario.ItemUsuarioDTO;
import co.edu.uniquindio.CRUD.excepciones.NoHayUsuariosException;
import co.edu.uniquindio.CRUD.repositorios.UsuarioRepository;
import co.edu.uniquindio.CRUD.servicios.interfaces.GeneralService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneralServiceImpl implements GeneralService {

    private final UsuarioRepository usuarioRepository;
    @Override
    public Page<ItemUsuarioDTO> listarUsuarios(Pageable pageable) throws Exception {
        Page<Usuario> usuariosPage = usuarioRepository.findAll(pageable);

        if (usuariosPage.isEmpty()) {
            throw new NoHayUsuariosException("No hay usuarios");
        }

        List<ItemUsuarioDTO> usuariosDTO = usuariosPage.getContent().stream()
                .map(usuario -> new ItemUsuarioDTO(
                        usuario.getCodigo(),
                        usuario.getNombre(),
                        usuario.getApellido()))
                .collect(Collectors.toList());

        return new PageImpl<>(usuariosDTO, pageable, usuariosPage.getTotalElements());
    }

}
