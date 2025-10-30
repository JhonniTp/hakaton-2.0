package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.RegistroUsuarioDTO;
import com.hakaton.hakaton.model.UsuarioModel;
import java.util.List;

public interface UsuarioService {

    List<UsuarioModel> obtenerTodosLosUsuarios();

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @param registroDTO Datos del usuario a registrar
     * @return Usuario registrado
     * @throws IllegalArgumentException si el correo ya existe o las contraseñas no
     *                                  coinciden
     */
    UsuarioModel registrarUsuario(RegistroUsuarioDTO registroDTO);

    /**
     * Verifica si existe un usuario con el correo electrónico proporcionado
     * 
     * @param correoElectronico Correo a verificar
     * @return true si existe, false si no
     */
    boolean existeCorreoElectronico(String correoElectronico);

}
