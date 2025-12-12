package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.ActividadModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActividadRepository extends JpaRepository<ActividadModel, Long> {

    List<ActividadModel> findTop20ByOrderByFechaActividadDesc();

    List<ActividadModel> findByTipoOrderByFechaActividadDesc(ActividadModel.TipoActividad tipo);

    List<ActividadModel> findByHackatonIdHackatonOrderByFechaActividadDesc(Long idHackaton);
}
