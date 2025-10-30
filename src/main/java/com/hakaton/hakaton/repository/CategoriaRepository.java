package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {

  /**
   * Busca una categoría por su nombre exacto
   */
  Optional<CategoriaModel> findByNombreCategoria(String nombreCategoria);

  /**
   * Verifica si existe una categoría con ese nombre
   */
  boolean existsByNombreCategoriaIgnoreCase(String nombreCategoria);

  /**
   * Busca categorías por nombre (búsqueda parcial, case-insensitive)
   */
  Optional<CategoriaModel> findByNombreCategoriaIgnoreCase(String nombreCategoria);
}
