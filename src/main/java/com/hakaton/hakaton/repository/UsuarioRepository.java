package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByCorreoElectronico(String correoElectronico);
    Optional<UsuarioModel> findByGoogleId(String googleId);
    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByTelefono(String telefono);
    boolean existsByGoogleId(String googleId);
    boolean existsByDocumentoDni(String documentoDni);
    
    @Modifying
    @Query("UPDATE UsuarioModel u SET u.rol = :rol WHERE u.idUsuario = :id")
    void actualizarRol(@Param("id") Long id, @Param("rol") UsuarioModel.Rol rol);
}
