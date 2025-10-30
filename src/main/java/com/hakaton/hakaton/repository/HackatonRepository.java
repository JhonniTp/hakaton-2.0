package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.CategoriaModel;
import com.hakaton.hakaton.model.HackatonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HackatonRepository extends JpaRepository<HackatonModel, Long> {

  /**
   * Busca hackatones por estado
   */
  List<HackatonModel> findByEstado(HackatonModel.Estado estado);

  /**
   * Busca hackatones por categoría
   */
  List<HackatonModel> findByCategoria(CategoriaModel categoria);

  /**
   * Busca hackatones por nombre (búsqueda parcial, case-insensitive)
   */
  List<HackatonModel> findByNombreContainingIgnoreCase(String nombre);

  /**
   * Busca hackatones cuya fecha de inicio esté entre dos fechas
   */
  List<HackatonModel> findByFechaInicioBetween(LocalDateTime inicio, LocalDateTime fin);

  /**
   * Busca hackatones que estén próximos o en curso (disponibles para inscripción)
   */
  @Query("SELECT h FROM HackatonModel h WHERE h.estado IN ('PROXIMO', 'EN_CURSO') ORDER BY h.fechaInicio ASC")
  List<HackatonModel> findHackatonesDisponibles();

  /**
   * Cuenta el número de hackatones por estado
   */
  Long countByEstado(HackatonModel.Estado estado);

  /**
   * Busca hackatones por estado y ordenados por fecha de inicio
   */
  List<HackatonModel> findByEstadoOrderByFechaInicioAsc(HackatonModel.Estado estado);

  /**
   * Busca todos los hackatones ordenados por fecha de creación descendente
   */
  List<HackatonModel> findAllByOrderByFechaCreacionDesc();

  /**
   * Verifica si existe un hackaton con el mismo nombre (para evitar duplicados)
   */
  boolean existsByNombreIgnoreCase(String nombre);
}
