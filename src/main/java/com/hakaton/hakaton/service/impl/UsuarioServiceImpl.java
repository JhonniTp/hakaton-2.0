package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UsuarioModel> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
}