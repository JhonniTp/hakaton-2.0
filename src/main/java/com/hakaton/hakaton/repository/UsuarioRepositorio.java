package com.hakaton.hakaton.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hakaton.hakaton.model.Usuario;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {
    
    // Spring Data JPA creará automáticamente la consulta para este método
    // basándose en el nombre del método.
    Optional<Usuario> findByEmail(String email);
}
