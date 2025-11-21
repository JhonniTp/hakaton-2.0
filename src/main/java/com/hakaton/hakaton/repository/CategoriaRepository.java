package com.hakaton.hakaton.repository;

import com.hakaton.hakaton.model.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {
    
    boolean existsByNombreCategoria(String nombreCategoria);
}
