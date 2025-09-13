package com.hakaton.hakaton.service;

import com.hakaton.hakaton.model.CategoriaModel;
import java.util.List;
import java.util.Optional;

public interface CategoriaService {
    
    // Crear una nueva categoría
    CategoriaModel crearCategoria(CategoriaModel categoria);
    
    // Obtener todas las categorías
    List<CategoriaModel> obtenerTodasLasCategorias();
    
    // Obtener categoría por ID
    Optional<CategoriaModel> obtenerCategoriaPorId(Long id);
    
    // Obtener categoría por nombre
    Optional<CategoriaModel> obtenerCategoriaPorNombre(String nombre);
    
    // Buscar categorías por nombre (búsqueda parcial)
    List<CategoriaModel> buscarCategoriasPorNombre(String nombre);
    
    // Actualizar una categoría existente
    CategoriaModel actualizarCategoria(Long id, CategoriaModel categoriaActualizada);
    
    // Eliminar una categoría por ID
    void eliminarCategoria(Long id);
    
    // Verificar si existe una categoría con el nombre
    boolean existeCategoriaConNombre(String nombre);
    
    // Obtener categorías más populares
    List<Object[]> obtenerCategoriasMasPopulares();
}