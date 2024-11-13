package co.edu.uniquindio.talleruno.services.interfaces;

import co.edu.uniquindio.talleruno.dtos.general.LoginDTO;
import co.edu.uniquindio.talleruno.dtos.autenticacionJwt.TokenDTO;

public interface AutenticacionServicio {

    TokenDTO login(LoginDTO loginDTO) throws Exception;
}
