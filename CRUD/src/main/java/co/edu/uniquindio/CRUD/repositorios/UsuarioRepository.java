package co.edu.uniquindio.CRUD.repositorios;

import co.edu.uniquindio.CRUD.documentos.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    @Query("{'idUsuario': ?0}")
    Optional<Usuario> obtenerUsuarioPorId(String idUsuario);
}


