package com.hakaton.hakaton.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hakaton.hakaton.model.Usuario;
import com.hakaton.hakaton.repository.UsuarioRepositorio;
import com.hakaton.hakaton.service.UsuarioServicio;

@Service
public class UsuarioServicioImpl implements UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public Usuario guardarOActualizarUsuario(Usuario usuario) {
        // Lógica para manejar el registro/login con Google.
        // Si el usuario ya existe, lo actualiza. Si no, lo crea.
        Optional<Usuario> usuarioExistente = usuarioRepositorio.findByEmail(usuario.getEmail());

        if (usuarioExistente.isPresent()) {
            Usuario user = usuarioExistente.get();
            user.setNombre(usuario.getNombre());
            user.setUrlFotoPerfil(usuario.getUrlFotoPerfil());
            // No se actualiza el rol ni otros datos sensibles al iniciar sesión.
            return usuarioRepositorio.save(user);
        } else {
            // Se podría asignar un rol por defecto para nuevos usuarios
            return usuarioRepositorio.save(usuario);
        }
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email);
    }
    
    @Override
    public Optional<Usuario> obtenerPorId(Integer id) {
        return usuarioRepositorio.findById(id);
    }
}