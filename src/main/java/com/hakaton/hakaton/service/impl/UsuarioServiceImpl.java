package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.dto.RegistroUsuarioDTO;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public UsuarioModel registrarUsuario(RegistroUsuarioDTO registroDTO) {
        // Validar que las contraseñas coincidan
        if (!registroDTO.passwordsCoinciden()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Verificar que el correo no exista
        if (usuarioRepository.existsByCorreoElectronico(registroDTO.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo electrónico ya está registrado");
        }

        // Verificar DNI si se proporciona
        if (registroDTO.getDocumentoDni() != null && !registroDTO.getDocumentoDni().isEmpty()) {
            if (usuarioRepository.existsByDocumentoDni(registroDTO.getDocumentoDni())) {
                throw new IllegalArgumentException("El número de documento ya está registrado");
            }
        }

        // Crear nuevo usuario
        UsuarioModel nuevoUsuario = new UsuarioModel();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellido(registroDTO.getApellido());
        nuevoUsuario.setCorreoElectronico(registroDTO.getCorreoElectronico());
        nuevoUsuario.setContrasenaHash(passwordEncoder.encode(registroDTO.getPassword()));
        nuevoUsuario.setDocumentoDni(registroDTO.getDocumentoDni());
        nuevoUsuario.setTelefono(registroDTO.getTelefono());
        nuevoUsuario.setPerfilExperiencia(registroDTO.getPerfilExperiencia());
        nuevoUsuario.setRol(UsuarioModel.Rol.PARTICIPANTE); // Por defecto es participante

        return usuarioRepository.save(nuevoUsuario);
    }

    @Override
    public boolean existeCorreoElectronico(String correoElectronico) {
        return usuarioRepository.existsByCorreoElectronico(correoElectronico);
    }
}