package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.CategoriaDTO;
import com.hakaton.hakaton.model.CategoriaModel;

import java.util.List;

/**
 * Servicio para la gestión de Categorías
 */
public interface CategoriaService {

  /**
   * Crea una nueva categoría
   * 
   * @param categoriaDTO Datos de la categoría
   * @return Categoría creada
   */
  CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO);

  /**
   * Obtiene una categoría por ID
   * 
   * @param id ID de la categoría
   * @return Categoría encontrada
   */
  CategoriaDTO obtenerPorId(Long id);

  /**
   * Lista todas las categorías
   * 
   * @return Lista de todas las categorías
   */
  List<CategoriaDTO> listarTodas();

  /**
   * Actualiza una categoría
   * 
   * @param id           ID de la categoría
   * @param categoriaDTO Nuevos datos
   * @return Categoría actualizada
   */
  CategoriaDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO);

  /**
   * Elimina una categoría
   * 
   * @param id ID de la categoría
   */
  void eliminarCategoria(Long id);

  /**
   * Obtiene el modelo de categoría (para uso interno)
   * 
   * @param id ID de la categoría
   * @return CategoriaModel
   */
  CategoriaModel obtenerModeloPorId(Long id);
}
