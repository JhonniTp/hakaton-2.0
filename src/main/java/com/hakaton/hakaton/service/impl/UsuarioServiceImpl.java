package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<UsuarioModel> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
}
