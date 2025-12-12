package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.ProyectoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<ProyectoModel, Long> {

    long countByFechaEntregaAfter(LocalDateTime fecha);

    List<ProyectoModel> findByEquipoHackatonIdHackaton(Long idHackaton);

    ProyectoModel findByEquipoIdEquipo(Long idEquipo);
}
