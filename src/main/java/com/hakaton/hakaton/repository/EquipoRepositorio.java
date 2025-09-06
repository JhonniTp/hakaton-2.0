package com.hakaton.hakaton.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hakaton.hakaton.model.Equipo;

@Repository
public interface EquipoRepositorio extends JpaRepository<Equipo, Integer> {
    // Por ahora no se necesitan métodos personalizados, pero se pueden añadir aquí.
}
