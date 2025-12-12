package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {
    Optional<UsuarioModel> findByCorreoElectronico(String correoElectronico);

    Optional<UsuarioModel> findByGoogleId(String googleId);

    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByTelefono(String telefono);

    boolean existsByGoogleId(String googleId);

    boolean existsByDocumentoDni(String documentoDni);

    List<UsuarioModel> findByRol(UsuarioModel.Rol rol);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM UsuarioModel u WHERE u.idUsuario != :id AND LOWER(u.correoElectronico) = LOWER(:correoElectronico)")
    boolean existsByCorreoElectronicoAndIdNot(@Param("correoElectronico") String correoElectronico,
            @Param("id") Long id);

    long countByRol(UsuarioModel.Rol rol);

    long countByFechaCreacionAfter(LocalDateTime fecha);

    @Modifying
    @Query("UPDATE UsuarioModel u SET u.rol = :rol WHERE u.idUsuario = :id")
    void actualizarRol(@Param("id") Long id, @Param("rol") UsuarioModel.Rol rol);
}
