package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.CriterioEvaluacionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CriterioEvaluacionRepository extends JpaRepository<CriterioEvaluacionModel, Long> {

    List<CriterioEvaluacionModel> findAllByOrderByPesoDesc();

    boolean existsByNombreCriterio(String nombreCriterio);

    @Query("SELECT SUM(c.peso) FROM CriterioEvaluacionModel c")
    BigDecimal sumAllPesos();

    @Query("SELECT COUNT(e) FROM EvaluacionModel e WHERE e.criterio.idCriterio = :idCriterio")
    long countEvaluacionesByCriterio(Long idCriterio);
}
