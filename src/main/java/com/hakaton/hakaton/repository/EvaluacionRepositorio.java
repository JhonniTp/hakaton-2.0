package com.hakaton.hakaton.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hakaton.hakaton.model.Evaluacion;

@Repository
public interface EvaluacionRepositorio extends JpaRepository<Evaluacion, Integer> {
    // Por ahora no se necesitan métodos personalizados.
}
