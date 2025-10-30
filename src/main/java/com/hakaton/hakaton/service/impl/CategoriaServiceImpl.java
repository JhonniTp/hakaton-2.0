package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.dto.CategoriaDTO;
import com.hakaton.hakaton.model.CategoriaModel;
import com.hakaton.hakaton.repository.CategoriaRepository;
import com.hakaton.hakaton.service.CategoriaService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

  private final CategoriaRepository categoriaRepository;

  public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
    this.categoriaRepository = categoriaRepository;
  }

  @Override
  public CategoriaDTO crearCategoria(CategoriaDTO categoriaDTO) {
    // Validar que no existe una categoría con el mismo nombre
    if (categoriaRepository.existsByNombreCategoriaIgnoreCase(categoriaDTO.getNombreCategoria())) {
      throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + categoriaDTO.getNombreCategoria());
    }

    CategoriaModel categoria = new CategoriaModel();
    categoria.setNombreCategoria(categoriaDTO.getNombreCategoria());

    CategoriaModel categoriaGuardada = categoriaRepository.save(categoria);

    return convertirADTO(categoriaGuardada);
  }

  @Override
  public CategoriaDTO obtenerPorId(Long id) {
    CategoriaModel categoria = categoriaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

    return convertirADTO(categoria);
  }

  @Override
  public List<CategoriaDTO> listarTodas() {
    return categoriaRepository.findAll()
        .stream()
        .map(this::convertirADTO)
        .collect(Collectors.toList());
  }

  @Override
  public CategoriaDTO actualizarCategoria(Long id, CategoriaDTO categoriaDTO) {
    CategoriaModel categoria = categoriaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

    // Validar que no existe otra categoría con el mismo nombre
    categoriaRepository.findByNombreCategoriaIgnoreCase(categoriaDTO.getNombreCategoria())
        .ifPresent(c -> {
          if (!c.getIdCategoria().equals(id)) {
            throw new IllegalArgumentException(
                "Ya existe otra categoría con el nombre: " + categoriaDTO.getNombreCategoria());
          }
        });

    categoria.setNombreCategoria(categoriaDTO.getNombreCategoria());

    CategoriaModel categoriaActualizada = categoriaRepository.save(categoria);

    return convertirADTO(categoriaActualizada);
  }

  @Override
  public void eliminarCategoria(Long id) {
    CategoriaModel categoria = categoriaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

    // TODO: Validar que no tiene hackatones asociados antes de eliminar
    // Long hackatones = hackatonRepository.countByCategoria(categoria);
    // if (hackatones > 0) {
    // throw new IllegalStateException("No se puede eliminar una categoría con
    // hackatones asociados");
    // }

    categoriaRepository.delete(categoria);
  }

  @Override
  public CategoriaModel obtenerModeloPorId(Long id) {
    return categoriaRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
  }

  /**
   * Convierte un CategoriaModel a CategoriaDTO
   */
  private CategoriaDTO convertirADTO(CategoriaModel categoria) {
    return new CategoriaDTO(
        categoria.getIdCategoria(),
        categoria.getNombreCategoria());
  }
}
