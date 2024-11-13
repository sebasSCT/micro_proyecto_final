package co.edu.uniquindio.talleruno.services.interfaces;

import co.edu.uniquindio.talleruno.dtos.general.EmailDTO;
import co.edu.uniquindio.talleruno.excepciones.ServidorCorreoNoDisponibleException;

public interface EmailService {

    void enviarEmail(EmailDTO emailDTO) throws Exception, ServidorCorreoNoDisponibleException;
}