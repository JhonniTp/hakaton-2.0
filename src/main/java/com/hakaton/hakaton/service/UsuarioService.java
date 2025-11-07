package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.UsuarioDTO;
import com.hakaton.hakaton.model.UsuarioModel;
import java.util.List;
import java.util.Optional;


public interface UsuarioService {

    List<UsuarioModel> obtenerTodosLosUsuarios();
    
    Optional<UsuarioModel> obtenerUsuarioPorId(Long id);
    
    UsuarioModel crearUsuario(UsuarioDTO usuarioDTO);
    
    UsuarioModel actualizarUsuario(Long id, UsuarioDTO usuarioDTO);
    
    void eliminarUsuario(Long id);
    
    boolean existeCorreoElectronico(String correoElectronico);
    
    boolean existeTelefono(String telefono);

}
