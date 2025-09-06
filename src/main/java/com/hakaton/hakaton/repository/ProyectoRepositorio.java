package com.hakaton.hakaton.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hakaton.hakaton.model.Proyecto;

@Repository
public interface ProyectoRepositorio extends JpaRepository<Proyecto, Integer> {
    
}
