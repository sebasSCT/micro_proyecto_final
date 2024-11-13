package co.edu.uniquindio.talleruno.services.interfaces;

import co.edu.uniquindio.talleruno.dtos.usuario.ItemUsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeneralService {

    Page<ItemUsuarioDTO> listarUsuarios(Pageable pageable) throws Exception;

    void enviarLinkRecuperacion(String email) throws Exception;
}
