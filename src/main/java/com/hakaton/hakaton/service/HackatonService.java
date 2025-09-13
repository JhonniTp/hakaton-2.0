package com.hakaton.hakaton.service;

import com.hakaton.hakaton.model.HackatonModel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HackatonService {
    
    // Crear un nuevo hackaton
    HackatonModel crearHackaton(HackatonModel hackaton);
    
    // Obtener todos los hackatones
    List<HackatonModel> obtenerTodosLosHackatones();
    
    // Obtener hackaton por ID
    Optional<HackatonModel> obtenerHackatonPorId(Long id);
    
    // Obtener hackatones por estado
    List<HackatonModel> obtenerHackatonesPorEstado(HackatonModel.Estado estado);
    
    // Obtener hackatones por categoría
    List<HackatonModel> obtenerHackatonesPorCategoria(Long categoriaId);
    
    // Buscar hackatones por nombre (búsqueda parcial)
    List<HackatonModel> buscarHackatonesPorNombre(String nombre);
    
    // Obtener hackatones activos (entre fecha de inicio y fin)
    List<HackatonModel> obtenerHackatonesActivos();
    
    // Obtener hackatones por jurado asignado
    List<HackatonModel> obtenerHackatonesPorJurado(Long juradoId);
    
    // Obtener hackatones con plazas disponibles
    List<HackatonModel> obtenerHackatonesConPlazasDisponibles();
    
    // Obtener hackatones por rango de fechas
    List<HackatonModel> obtenerHackatonesPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    // Actualizar un hackaton existente
    HackatonModel actualizarHackaton(Long id, HackatonModel hackatonActualizado);
    
    // Eliminar un hackaton por ID
    void eliminarHackaton(Long id);
    
    // Obtener estadísticas de hackatones por estado
    List<Object[]> obtenerEstadisticasPorEstado();
}