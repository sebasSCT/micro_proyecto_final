package co.edu.uniquindio.talleruno.dtos.usuario;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public record PageItemUsuarioDTO(
        List<ItemUsuarioDTO> content,
        Pageable pageable,
        int totalPages,
        long totalElements,
        boolean last,
        int size,
        int number,
        Sort sort,
        int numberOfElements,
        boolean first,
        boolean empty
) {
}