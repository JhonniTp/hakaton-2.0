package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.EvaluacionModel;
import com.hakaton.hakaton.model.HackatonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<EvaluacionModel, Long> {

    @Query("SELECT COUNT(DISTINCT e.proyecto) FROM EvaluacionModel e " +
            "WHERE e.proyecto.equipo.hackaton.estado = :estado " +
            "AND e.feedback IS NULL")
    long countEvaluacionesPendientesByEstado(HackatonModel.Estado estado);

    List<EvaluacionModel> findByProyectoIdProyecto(Long idProyecto);

    List<EvaluacionModel> findByJuradoIdUsuario(Long idJurado);

    boolean existsByProyectoIdProyectoAndCriterioIdCriterioAndJuradoIdUsuario(
            Long idProyecto, Long idCriterio, Long idJurado);

    @Query("SELECT e FROM EvaluacionModel e WHERE e.proyecto.equipo.hackaton.idHackaton = :idHackaton")
    List<EvaluacionModel> findByHackatonId(@Param("idHackaton") Long idHackaton);

    @Query("SELECT AVG(e.puntuacion * e.criterio.peso) FROM EvaluacionModel e WHERE e.proyecto.idProyecto = :idProyecto")
    BigDecimal calcularPuntajePromedioProyecto(@Param("idProyecto") Long idProyecto);
}
