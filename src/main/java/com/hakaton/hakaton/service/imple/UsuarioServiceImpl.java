package com.hakaton.hakaton.service.imple;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UsuarioModel crearUsuario(UsuarioModel usuario) {
        // Validar que el email no exista
        if (usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())) {
            throw new RuntimeException("Ya existe un usuario con este email: " + usuario.getCorreoElectronico());
        }
        
        // Si tiene Google ID, validar que no exista
        if (usuario.getGoogleId() != null && 
            usuarioRepository.findByGoogleId(usuario.getGoogleId()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con este Google ID: " + usuario.getGoogleId());
        }
        
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioModel> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<UsuarioModel> obtenerUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<UsuarioModel> obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByCorreoElectronico(email);
    }

    @Override
    public Optional<UsuarioModel> obtenerUsuarioPorGoogleId(String googleId) {
        return usuarioRepository.findByGoogleId(googleId);
    }

    @Override
    public List<UsuarioModel> obtenerUsuariosPorRol(UsuarioModel.Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    @Override
    public List<UsuarioModel> buscarUsuariosPorNombreOApellido(String nombre) {
        return usuarioRepository.buscarPorNombreOApellido(nombre);
    }

    @Override
    public UsuarioModel actualizarUsuario(Long id, UsuarioModel usuarioActualizado) {
        return usuarioRepository.findById(id)
                .map(usuarioExistente -> {
                    // Validar que el nuevo email no esté en uso por otro usuario
                    if (!usuarioExistente.getCorreoElectronico().equals(usuarioActualizado.getCorreoElectronico()) &&
                        usuarioRepository.existsByCorreoElectronico(usuarioActualizado.getCorreoElectronico())) {
                        throw new RuntimeException("Ya existe un usuario con este email: " + usuarioActualizado.getCorreoElectronico());
                    }
                    
                    // Validar que el nuevo Google ID no esté en uso por otro usuario
                    if (usuarioActualizado.getGoogleId() != null && 
                        !usuarioActualizado.getGoogleId().equals(usuarioExistente.getGoogleId()) &&
                        usuarioRepository.findByGoogleId(usuarioActualizado.getGoogleId()).isPresent()) {
                        throw new RuntimeException("Ya existe un usuario con este Google ID: " + usuarioActualizado.getGoogleId());
                    }
                    
                    // Actualizar campos
                    usuarioExistente.setNombre(usuarioActualizado.getNombre());
                    usuarioExistente.setApellido(usuarioActualizado.getApellido());
                    usuarioExistente.setCorreoElectronico(usuarioActualizado.getCorreoElectronico());
                    usuarioExistente.setDocumentoDni(usuarioActualizado.getDocumentoDni());
                    usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
                    usuarioExistente.setGoogleId(usuarioActualizado.getGoogleId());
                    usuarioExistente.setRol(usuarioActualizado.getRol());
                    usuarioExistente.setPerfilExperiencia(usuarioActualizado.getPerfilExperiencia());
                    usuarioExistente.setUrlCodigoQr(usuarioActualizado.getUrlCodigoQr());
                    
                    return usuarioRepository.save(usuarioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean existeUsuarioConEmail(String email) {
        return usuarioRepository.existsByCorreoElectronico(email);
    }

    @Override
    public List<UsuarioModel> obtenerJuradosDisponibles() {
        return usuarioRepository.findJuradosDisponibles();
    }
}