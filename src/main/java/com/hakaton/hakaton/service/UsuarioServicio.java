package com.hakaton.hakaton.service;

import java.util.Optional;

import com.hakaton.hakaton.model.Usuario;

public interface UsuarioServicio {

    Usuario guardarOActualizarUsuario(Usuario usuario);
    
    Optional<Usuario> obtenerPorEmail(String email);

    Optional<Usuario> obtenerPorId(Integer id);
}

