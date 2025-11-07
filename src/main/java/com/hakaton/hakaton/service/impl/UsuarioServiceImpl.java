package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.dto.UsuarioDTO;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioModel> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioModel> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public UsuarioModel crearUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByCorreoElectronico(usuarioDTO.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }
        if (usuarioDTO.getTelefono() != null && !usuarioDTO.getTelefono().isBlank() 
            && usuarioRepository.existsByTelefono(usuarioDTO.getTelefono())) {
            throw new IllegalArgumentException("El teléfono ya está registrado");
        }

        UsuarioModel usuario = usuarioDTO.toUsuarioModel();
        
        if (usuarioDTO.getContrasena() != null && !usuarioDTO.getContrasena().isBlank()) {
            usuario.setContrasenaHash(passwordEncoder.encode(usuarioDTO.getContrasena()));
        }
        
        return usuarioRepository.save(usuario);
    }

    @Override
    public UsuarioModel actualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        UsuarioModel usuarioExistente = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        
        if (!usuarioExistente.getCorreoElectronico().equals(usuarioDTO.getCorreoElectronico())) {
            if (usuarioRepository.existsByCorreoElectronico(usuarioDTO.getCorreoElectronico())) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado");
            }
            usuarioExistente.setCorreoElectronico(usuarioDTO.getCorreoElectronico());
        }
        
        if (usuarioDTO.getTelefono() != null && !usuarioDTO.getTelefono().isBlank()) {
            if (!usuarioDTO.getTelefono().equals(usuarioExistente.getTelefono())) {
                if (usuarioRepository.existsByTelefono(usuarioDTO.getTelefono())) {
                    throw new IllegalArgumentException("El teléfono ya está registrado");
                }
                usuarioExistente.setTelefono(usuarioDTO.getTelefono());
            }
        } else {
            usuarioExistente.setTelefono(null);
        }
        
        usuarioExistente.setNombre(usuarioDTO.getNombre());
        usuarioExistente.setApellido(usuarioDTO.getApellido());
        usuarioExistente.setDocumentoDni(usuarioDTO.getDocumentoDni());
        usuarioExistente.setRol(usuarioDTO.getRol());
        usuarioExistente.setPerfilExperiencia(usuarioDTO.getPerfilExperiencia());
        
        if (usuarioDTO.getContrasena() != null && !usuarioDTO.getContrasena().isBlank()) {
            usuarioExistente.setContrasenaHash(passwordEncoder.encode(usuarioDTO.getContrasena()));
        }
        
        return usuarioRepository.save(usuarioExistente);
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCorreoElectronico(String correoElectronico) {
        return usuarioRepository.existsByCorreoElectronico(correoElectronico);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeTelefono(String telefono) {
        return usuarioRepository.existsByTelefono(telefono);
    }
}