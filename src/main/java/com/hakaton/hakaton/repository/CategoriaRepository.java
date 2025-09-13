package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
    
    // Buscar categoría por nombre
    Optional<CategoriaModel> findByNombreCategoria(String nombreCategoria);
    
    // Buscar categorías por nombre (búsqueda parcial)
    @Query("SELECT c FROM CategoriaModel c WHERE LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<CategoriaModel> buscarPorNombre(@Param("nombre") String nombre);
    
    // Verificar si existe una categoría con el nombre
    boolean existsByNombreCategoria(String nombreCategoria);
    
    // Obtener categorías más populares (con más hackatones)
    @Query("SELECT c, COUNT(h) as cantidad FROM CategoriaModel c LEFT JOIN c.hackatones h GROUP BY c ORDER BY cantidad DESC")
    List<Object[]> findCategoriasMasPopulares();
}