package com.hakaton.hakaton.service;

import com.hakaton.hakaton.model.UsuarioModel;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    
    // Crear un nuevo usuario
    UsuarioModel crearUsuario(UsuarioModel usuario);
    
    // Obtener todos los usuarios
    List<UsuarioModel> obtenerTodosLosUsuarios();
    
    // Obtener usuario por ID
    Optional<UsuarioModel> obtenerUsuarioPorId(Long id);
    
    // Obtener usuario por email
    Optional<UsuarioModel> obtenerUsuarioPorEmail(String email);
    
    // Obtener usuario por Google ID
    Optional<UsuarioModel> obtenerUsuarioPorGoogleId(String googleId);
    
    // Obtener usuarios por rol
    List<UsuarioModel> obtenerUsuariosPorRol(UsuarioModel.Rol rol);
    
    // Buscar usuarios por nombre o apellido
    List<UsuarioModel> buscarUsuariosPorNombreOApellido(String nombre);
    
    // Actualizar un usuario existente
    UsuarioModel actualizarUsuario(Long id, UsuarioModel usuarioActualizado);
    
    // Eliminar un usuario por ID
    void eliminarUsuario(Long id);
    
    // Verificar si existe un usuario con el email
    boolean existeUsuarioConEmail(String email);
    
    // Obtener jurados disponibles
    List<UsuarioModel> obtenerJuradosDisponibles();
}