package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.JuradoHackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JuradoHackatonRepository extends JpaRepository<JuradoHackatonModel, Long> {

  /**
   * Busca todos los jurados asignados a un hackatón específico
   */
  List<JuradoHackatonModel> findByHackaton(HackatonModel hackaton);

  /**
   * Busca todos los jurados asignados a un hackatón por ID
   */
  @Query("SELECT jh FROM JuradoHackatonModel jh WHERE jh.hackaton.idHackaton = :hackatonId")
  List<JuradoHackatonModel> findByHackatonId(@Param("hackatonId") Long hackatonId);

  /**
   * Busca todos los hackatones asignados a un jurado
   */
  List<JuradoHackatonModel> findByJurado(UsuarioModel jurado);

  /**
   * Verifica si un jurado ya está asignado a un hackatón
   */
  boolean existsByJuradoAndHackaton(UsuarioModel jurado, HackatonModel hackaton);

  /**
   * Verifica por IDs
   */
  @Query("SELECT CASE WHEN COUNT(jh) > 0 THEN true ELSE false END FROM JuradoHackatonModel jh " +
      "WHERE jh.jurado.idUsuario = :juradoId AND jh.hackaton.idHackaton = :hackatonId")
  boolean existsByJuradoIdAndHackatonId(@Param("juradoId") Long juradoId, @Param("hackatonId") Long hackatonId);

  /**
   * Elimina la asignación de un jurado a un hackatón
   */
  void deleteByJuradoAndHackaton(UsuarioModel jurado, HackatonModel hackaton);

  /**
   * Cuenta cuántos jurados tiene asignados un hackatón
   */
  @Query("SELECT COUNT(jh) FROM JuradoHackatonModel jh WHERE jh.hackaton.idHackaton = :hackatonId")
  Long countByHackatonId(@Param("hackatonId") Long hackatonId);
}
