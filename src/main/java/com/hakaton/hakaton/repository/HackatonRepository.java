package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.HackatonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HackatonRepository extends JpaRepository<HackatonModel, Long> {

    List<HackatonModel> findByEstado(HackatonModel.Estado estado);

    List<HackatonModel> findByCategoriaIdCategoria(Long idCategoria);

    @Query("SELECT h FROM HackatonModel h WHERE h.fechaInicio >= :fechaInicio AND h.fechaFin <= :fechaFin")
    List<HackatonModel> findByFechaRange(@Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT h FROM HackatonModel h WHERE LOWER(h.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<HackatonModel> findByNombreContaining(@Param("nombre") String nombre);

    boolean existsByNombre(String nombre);

    @Query("SELECT h FROM HackatonModel h WHERE h.idHackaton != :id AND LOWER(h.nombre) = LOWER(:nombre)")
    boolean existsByNombreAndIdNot(@Param("nombre") String nombre, @Param("id") Long id);
}
