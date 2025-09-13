package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    
    // Buscar usuario por email
    Optional<UsuarioModel> findByCorreoElectronico(String correoElectronico);
    
    // Buscar usuario por Google ID
    Optional<UsuarioModel> findByGoogleId(String googleId);
    
    // Buscar usuarios por rol
    List<UsuarioModel> findByRol(UsuarioModel.Rol rol);
    
    // Buscar usuarios por nombre o apellido (búsqueda parcial)
    @Query("SELECT u FROM UsuarioModel u WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR LOWER(u.apellido) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<UsuarioModel> buscarPorNombreOApellido(@Param("nombre") String nombre);
    
    // Verificar si existe un usuario con el email
    boolean existsByCorreoElectronico(String correoElectronico);
    
    // Buscar jurados disponibles (que no estén asignados a ningún hackaton)
    @Query("SELECT u FROM UsuarioModel u WHERE u.rol = 'jurado' AND u.idUsuario NOT IN (SELECT h.juradoAsignado.idUsuario FROM HackatonModel h WHERE h.juradoAsignado IS NOT NULL)")
    List<UsuarioModel> findJuradosDisponibles();
}