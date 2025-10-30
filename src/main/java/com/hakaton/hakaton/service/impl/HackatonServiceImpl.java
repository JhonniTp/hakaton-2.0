package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.dto.HackatonDTO;
import com.hakaton.hakaton.dto.HackatonResponseDTO;
import com.hakaton.hakaton.model.CategoriaModel;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.service.CategoriaService;
import com.hakaton.hakaton.service.HackatonService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HackatonServiceImpl implements HackatonService {

  private final HackatonRepository hackatonRepository;
  private final CategoriaService categoriaService;

  public HackatonServiceImpl(HackatonRepository hackatonRepository, CategoriaService categoriaService) {
    this.hackatonRepository = hackatonRepository;
    this.categoriaService = categoriaService;
  }

  @Override
  public HackatonResponseDTO crearHackaton(HackatonDTO hackatonDTO) {
    // Validar que no existe un hackaton con el mismo nombre
    if (hackatonRepository.existsByNombreIgnoreCase(hackatonDTO.getNombre())) {
      throw new IllegalArgumentException("Ya existe un hackatón con el nombre: " + hackatonDTO.getNombre());
    }

    // Validar que la fecha de fin sea después de la fecha de inicio
    if (hackatonDTO.getFechaFin().isBefore(hackatonDTO.getFechaInicio()) ||
        hackatonDTO.getFechaFin().isEqual(hackatonDTO.getFechaInicio())) {
      throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
    }

    // Obtener la categoría
    CategoriaModel categoria = categoriaService.obtenerModeloPorId(hackatonDTO.getIdCategoria());

    // Crear el modelo
    HackatonModel hackaton = new HackatonModel();
    hackaton.setNombre(hackatonDTO.getNombre());
    hackaton.setDescripcion(hackatonDTO.getDescripcion());
    hackaton.setUrlImg(hackatonDTO.getUrlImg());
    hackaton.setFechaInicio(hackatonDTO.getFechaInicio());
    hackaton.setFechaFin(hackatonDTO.getFechaFin());
    hackaton.setMaximoParticipantes(hackatonDTO.getMaximoParticipantes());
    hackaton.setGrupoCantidadParticipantes(hackatonDTO.getGrupoCantidadParticipantes());
    hackaton.setCategoria(categoria);

    // Determinar estado automáticamente basado en fechas
    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(hackatonDTO.getFechaInicio())) {
      hackaton.setEstado(HackatonModel.Estado.PROXIMO);
    } else if (now.isAfter(hackatonDTO.getFechaFin())) {
      hackaton.setEstado(HackatonModel.Estado.FINALIZADO);
    } else {
      hackaton.setEstado(HackatonModel.Estado.EN_CURSO);
    }

    // Guardar
    HackatonModel hackatonGuardado = hackatonRepository.save(hackaton);

    return new HackatonResponseDTO(hackatonGuardado);
  }

  @Override
  public HackatonResponseDTO actualizarHackaton(Long id, HackatonDTO hackatonDTO) {
    HackatonModel hackaton = hackatonRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado con ID: " + id));

    // Validar que no hay otro hackaton con el mismo nombre (excepto el actual)
    hackatonRepository.findByNombreContainingIgnoreCase(hackatonDTO.getNombre())
        .stream()
        .filter(h -> !h.getIdHackaton().equals(id))
        .findFirst()
        .ifPresent(h -> {
          throw new IllegalArgumentException("Ya existe otro hackatón con el nombre: " + hackatonDTO.getNombre());
        });

    // Validar fechas
    if (hackatonDTO.getFechaFin().isBefore(hackatonDTO.getFechaInicio()) ||
        hackatonDTO.getFechaFin().isEqual(hackatonDTO.getFechaInicio())) {
      throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
    }

    // Obtener la categoría
    CategoriaModel categoria = categoriaService.obtenerModeloPorId(hackatonDTO.getIdCategoria());

    // Actualizar campos
    hackaton.setNombre(hackatonDTO.getNombre());
    hackaton.setDescripcion(hackatonDTO.getDescripcion());
    hackaton.setUrlImg(hackatonDTO.getUrlImg());
    hackaton.setFechaInicio(hackatonDTO.getFechaInicio());
    hackaton.setFechaFin(hackatonDTO.getFechaFin());
    hackaton.setMaximoParticipantes(hackatonDTO.getMaximoParticipantes());
    hackaton.setGrupoCantidadParticipantes(hackatonDTO.getGrupoCantidadParticipantes());
    hackaton.setCategoria(categoria);

    // Actualizar estado automáticamente si es necesario
    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(hackatonDTO.getFechaInicio())) {
      hackaton.setEstado(HackatonModel.Estado.PROXIMO);
    } else if (now.isAfter(hackatonDTO.getFechaFin())) {
      hackaton.setEstado(HackatonModel.Estado.FINALIZADO);
    } else if (hackaton.getEstado() != HackatonModel.Estado.FINALIZADO) {
      hackaton.setEstado(HackatonModel.Estado.EN_CURSO);
    }

    HackatonModel hackatonActualizado = hackatonRepository.save(hackaton);

    return new HackatonResponseDTO(hackatonActualizado);
  }

  @Override
  public void eliminarHackaton(Long id) {
    HackatonModel hackaton = hackatonRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado con ID: " + id));

    // TODO: Cuando se implemente InscripcionRepository, validar que no tiene
    // inscripciones
    // Long inscripciones = inscripcionRepository.countByHackaton(hackaton);
    // if (inscripciones > 0) {
    // throw new IllegalStateException("No se puede eliminar un hackatón con
    // inscripciones");
    // }

    hackatonRepository.delete(hackaton);
  }

  @Override
  public HackatonResponseDTO obtenerPorId(Long id) {
    HackatonModel hackaton = hackatonRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado con ID: " + id));

    // TODO: Cuando se implemente InscripcionRepository, calcular inscritos
    HackatonResponseDTO response = new HackatonResponseDTO(hackaton);
    response.setTotalInscritos(0); // Placeholder
    response.setCuposDisponibles(hackaton.getMaximoParticipantes());
    response.setEstaCompleto(false);

    return response;
  }

  @Override
  public List<HackatonResponseDTO> listarTodos() {
    return hackatonRepository.findAllByOrderByFechaCreacionDesc()
        .stream()
        .map(HackatonResponseDTO::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<HackatonResponseDTO> listarPorEstado(HackatonModel.Estado estado) {
    return hackatonRepository.findByEstadoOrderByFechaInicioAsc(estado)
        .stream()
        .map(HackatonResponseDTO::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<HackatonResponseDTO> listarDisponibles() {
    return hackatonRepository.findHackatonesDisponibles()
        .stream()
        .map(HackatonResponseDTO::new)
        .collect(Collectors.toList());
  }

  @Override
  public List<HackatonResponseDTO> buscarPorNombre(String nombre) {
    return hackatonRepository.findByNombreContainingIgnoreCase(nombre)
        .stream()
        .map(HackatonResponseDTO::new)
        .collect(Collectors.toList());
  }

  @Override
  public HackatonResponseDTO cambiarEstado(Long id, HackatonModel.Estado nuevoEstado) {
    HackatonModel hackaton = hackatonRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado con ID: " + id));

    hackaton.setEstado(nuevoEstado);
    HackatonModel hackatonActualizado = hackatonRepository.save(hackaton);

    return new HackatonResponseDTO(hackatonActualizado);
  }

  @Override
  public Long contarPorEstado(HackatonModel.Estado estado) {
    return hackatonRepository.countByEstado(estado);
  }
}
