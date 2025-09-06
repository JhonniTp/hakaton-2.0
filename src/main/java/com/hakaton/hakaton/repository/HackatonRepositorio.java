package com.hakaton.hakaton.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hakaton.hakaton.model.Hackaton;
import com.hakaton.hakaton.model.enums.EstadoHackaton;

@Repository
public interface HackatonRepositorio extends JpaRepository<Hackaton, Integer> {

    // Método de consulta personalizado para buscar hackatones por su estado.
    List<Hackaton> findByEstado(EstadoHackaton estado);
}
