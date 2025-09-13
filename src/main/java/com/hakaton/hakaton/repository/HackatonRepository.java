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
    
    // Buscar hackatones por estado
    List<HackatonModel> findByEstado(HackatonModel.Estado estado);
    
    // Buscar hackatones por categoría
    List<HackatonModel> findByCategoriaId(Long categoriaId);
    
    // Buscar hackatones por nombre (búsqueda parcial)
    @Query("SELECT h FROM HackatonModel h WHERE LOWER(h.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<HackatonModel> buscarPorNombre(@Param("nombre") String nombre);
    
    // Buscar hackatones por fecha de inicio posterior a una fecha dada
    List<HackatonModel> findByFechaInicioAfter(LocalDateTime fecha);
    
    // Buscar hackatones por fecha de fin anterior a una fecha dada
    List<HackatonModel> findByFechaFinBefore(LocalDateTime fecha);
    
    // Buscar hackatones activos (entre fecha de inicio y fin)
    @Query("SELECT h FROM HackatonModel h WHERE h.fechaInicio <= :fecha AND h.fechaFin >= :fecha")
    List<HackatonModel> findHackatonesActivos(@Param("fecha") LocalDateTime fecha);
    
    // Buscar hackatones por jurado asignado
    List<HackatonModel> findByJuradoAsignadoId(Long juradoId);
    
    // Contar hackatones por estado
    @Query("SELECT h.estado, COUNT(h) FROM HackatonModel h GROUP BY h.estado")
    List<Object[]> contarPorEstado();
    
    // Buscar hackatones con plazas disponibles
    @Query("SELECT h FROM HackatonModel h WHERE h.maximoParticipantes > (SELECT COUNT(i) FROM InscripcionModel i WHERE i.hackaton.idHackaton = h.idHackaton) AND h.estado = 'proximo'")
    List<HackatonModel> findHackatonesConPlazasDisponibles();
    
    // Buscar hackatones por rango de fechas
    @Query("SELECT h FROM HackatonModel h WHERE h.fechaInicio BETWEEN :fechaInicio AND :fechaFin OR h.fechaFin BETWEEN :fechaInicio AND :fechaFin")
    List<HackatonModel> findHackatonesPorRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}