package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.HackatonDTO;
import com.hakaton.hakaton.dto.HackatonResponseDTO;
import com.hakaton.hakaton.model.HackatonModel;

import java.util.List;

/**
 * Servicio para la gestión de Hackatones
 */
public interface HackatonService {

  /**
   * Crea un nuevo hackatón
   * 
   * @param hackatonDTO Datos del hackatón a crear
   * @return Hackatón creado con información completa
   */
  HackatonResponseDTO crearHackaton(HackatonDTO hackatonDTO);

  /**
   * Actualiza un hackatón existente
   * 
   * @param id          ID del hackatón a actualizar
   * @param hackatonDTO Nuevos datos del hackatón
   * @return Hackatón actualizado
   */
  HackatonResponseDTO actualizarHackaton(Long id, HackatonDTO hackatonDTO);

  /**
   * Elimina un hackatón
   * 
   * @param id ID del hackatón a eliminar
   * @throws IllegalStateException si el hackatón tiene inscripciones
   */
  void eliminarHackaton(Long id);

  /**
   * Obtiene un hackatón por ID
   * 
   * @param id ID del hackatón
   * @return Hackatón encontrado
   */
  HackatonResponseDTO obtenerPorId(Long id);

  /**
   * Lista todos los hackatones
   * 
   * @return Lista de todos los hackatones
   */
  List<HackatonResponseDTO> listarTodos();

  /**
   * Lista hackatones por estado
   * 
   * @param estado Estado del hackatón (PROXIMO, EN_CURSO, FINALIZADO)
   * @return Lista de hackatones filtrados por estado
   */
  List<HackatonResponseDTO> listarPorEstado(HackatonModel.Estado estado);

  /**
   * Lista hackatones disponibles para inscripción (PROXIMO y EN_CURSO)
   * 
   * @return Lista de hackatones disponibles
   */
  List<HackatonResponseDTO> listarDisponibles();

  /**
   * Busca hackatones por nombre
   * 
   * @param nombre Nombre o parte del nombre a buscar
   * @return Lista de hackatones que coinciden
   */
  List<HackatonResponseDTO> buscarPorNombre(String nombre);

  /**
   * Cambia el estado de un hackatón
   * 
   * @param id          ID del hackatón
   * @param nuevoEstado Nuevo estado
   * @return Hackatón con estado actualizado
   */
  HackatonResponseDTO cambiarEstado(Long id, HackatonModel.Estado nuevoEstado);

  /**
   * Cuenta hackatones por estado
   * 
   * @param estado Estado a contar
   * @return Número de hackatones en ese estado
   */
  Long contarPorEstado(HackatonModel.Estado estado);
}
