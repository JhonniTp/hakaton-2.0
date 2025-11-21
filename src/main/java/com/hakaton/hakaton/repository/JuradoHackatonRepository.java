package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.JuradoHackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JuradoHackatonRepository extends JpaRepository<JuradoHackatonModel, Long> {

    List<JuradoHackatonModel> findByJurado(UsuarioModel jurado);
    @Query("SELECT jh FROM JuradoHackatonModel jh WHERE jh.jurado.idUsuario = :idJurado")
    List<JuradoHackatonModel> findByJuradoId(@Param("idJurado") Long idJurado);
    List<JuradoHackatonModel> findByHackaton(HackatonModel hackaton);
    @Query("SELECT jh FROM JuradoHackatonModel jh WHERE jh.hackaton.idHackaton = :idHackaton")
    List<JuradoHackatonModel> findByHackatonId(@Param("idHackaton") Long idHackaton);
    @Query("SELECT jh FROM JuradoHackatonModel jh WHERE jh.jurado.idUsuario = :idJurado AND jh.hackaton.idHackaton = :idHackaton")
    Optional<JuradoHackatonModel> findByJuradoIdAndHackatonId(@Param("idJurado") Long idJurado,
            @Param("idHackaton") Long idHackaton);
    @Query("SELECT COUNT(jh) > 0 FROM JuradoHackatonModel jh WHERE jh.jurado.idUsuario = :idJurado AND jh.hackaton.idHackaton = :idHackaton")
    boolean existsByJuradoIdAndHackatonId(@Param("idJurado") Long idJurado, @Param("idHackaton") Long idHackaton);
    @Query("SELECT COUNT(jh) FROM JuradoHackatonModel jh WHERE jh.hackaton.idHackaton = :idHackaton")
    long countByHackatonId(@Param("idHackaton") Long idHackaton);
    @Query("SELECT COUNT(jh) FROM JuradoHackatonModel jh WHERE jh.jurado.idUsuario = :idJurado")
    long countByJuradoId(@Param("idJurado") Long idJurado);
    @Query("SELECT jh FROM JuradoHackatonModel jh " +
            "JOIN FETCH jh.hackaton h " +
            "JOIN FETCH jh.jurado j " +
            "WHERE j.idUsuario = :idJurado " +
            "ORDER BY h.fechaInicio DESC")
    List<JuradoHackatonModel> findHackatonesWithDetailsByJuradoId(@Param("idJurado") Long idJurado);
    @Query("SELECT jh FROM JuradoHackatonModel jh " +
            "JOIN FETCH jh.jurado j " +
            "JOIN FETCH jh.hackaton h " +
            "WHERE h.idHackaton = :idHackaton " +
            "ORDER BY jh.fechaAsignacion DESC")
    List<JuradoHackatonModel> findJuradosWithDetailsByHackatonId(@Param("idHackaton") Long idHackaton);
    void deleteByJurado(UsuarioModel jurado);
    void deleteByHackaton(HackatonModel hackaton);
}
