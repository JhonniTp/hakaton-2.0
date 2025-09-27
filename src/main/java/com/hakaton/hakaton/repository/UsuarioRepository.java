package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

}