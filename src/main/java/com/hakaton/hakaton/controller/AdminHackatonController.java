package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.CategoriaDTO;
import com.hakaton.hakaton.dto.HackatonDTO;
import com.hakaton.hakaton.dto.HackatonResponseDTO;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.service.CategoriaService;
import com.hakaton.hakaton.service.HackatonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de hackatones por parte del administrador
 */
@RestController
@RequestMapping("/admin/hackatones")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminHackatonController {

  private final HackatonService hackatonService;
  private final CategoriaService categoriaService;

  public AdminHackatonController(HackatonService hackatonService, CategoriaService categoriaService) {
    this.hackatonService = hackatonService;
    this.categoriaService = categoriaService;
  }

  /**
   * Obtener todos los hackatones
   */
  @GetMapping
  public ResponseEntity<List<HackatonResponseDTO>> listarTodos(
      @RequestParam(required = false) String estado,
      @RequestParam(required = false) String nombre) {

    List<HackatonResponseDTO> hackatones;

    if (estado != null && !estado.isEmpty() && !estado.equals("all")) {
      try {
        HackatonModel.Estado estadoEnum = HackatonModel.Estado.valueOf(estado.toUpperCase());
        hackatones = hackatonService.listarPorEstado(estadoEnum);
      } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
      }
    } else if (nombre != null && !nombre.isEmpty()) {
      hackatones = hackatonService.buscarPorNombre(nombre);
    } else {
      hackatones = hackatonService.listarTodos();
    }

    return ResponseEntity.ok(hackatones);
  }

  /**
   * Obtener un hackatón por ID
   */
  @GetMapping("/{id}")
  public ResponseEntity<HackatonResponseDTO> obtenerPorId(@PathVariable Long id) {
    try {
      HackatonResponseDTO hackaton = hackatonService.obtenerPorId(id);
      return ResponseEntity.ok(hackaton);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Crear un nuevo hackatón
   */
  @PostMapping
  public ResponseEntity<?> crear(@Valid @RequestBody HackatonDTO hackatonDTO, BindingResult bindingResult) {
    Map<String, Object> response = new HashMap<>();

    // Validar errores de validación
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = bindingResult.getFieldErrors().stream()
          .collect(Collectors.toMap(
              error -> error.getField(),
              error -> error.getDefaultMessage()));
      response.put("success", false);
      response.put("errors", errors);
      return ResponseEntity.badRequest().body(response);
    }

    try {
      HackatonResponseDTO hackatonCreado = hackatonService.crearHackaton(hackatonDTO);
      response.put("success", true);
      response.put("message", "Hackatón creado exitosamente");
      response.put("hackaton", hackatonCreado);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
      response.put("success", false);
      response.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al crear el hackatón: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Actualizar un hackatón existente
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> actualizar(
      @PathVariable Long id,
      @Valid @RequestBody HackatonDTO hackatonDTO,
      BindingResult bindingResult) {

    Map<String, Object> response = new HashMap<>();

    // Validar errores de validación
    if (bindingResult.hasErrors()) {
      Map<String, String> errors = bindingResult.getFieldErrors().stream()
          .collect(Collectors.toMap(
              error -> error.getField(),
              error -> error.getDefaultMessage()));
      response.put("success", false);
      response.put("errors", errors);
      return ResponseEntity.badRequest().body(response);
    }

    try {
      HackatonResponseDTO hackatonActualizado = hackatonService.actualizarHackaton(id, hackatonDTO);
      response.put("success", true);
      response.put("message", "Hackatón actualizado exitosamente");
      response.put("hackaton", hackatonActualizado);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      response.put("success", false);
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al actualizar el hackatón: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Eliminar un hackatón
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<?> eliminar(@PathVariable Long id) {
    Map<String, Object> response = new HashMap<>();

    try {
      hackatonService.eliminarHackaton(id);
      response.put("success", true);
      response.put("message", "Hackatón eliminado exitosamente");
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      response.put("success", false);
      response.put("message", "Hackatón no encontrado");
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    } catch (IllegalStateException e) {
      response.put("success", false);
      response.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al eliminar el hackatón: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Cambiar el estado de un hackatón
   */
  @PatchMapping("/{id}/estado")
  public ResponseEntity<?> cambiarEstado(
      @PathVariable Long id,
      @RequestParam String estado) {

    Map<String, Object> response = new HashMap<>();

    try {
      HackatonModel.Estado nuevoEstado = HackatonModel.Estado.valueOf(estado.toUpperCase());
      HackatonResponseDTO hackatonActualizado = hackatonService.cambiarEstado(id, nuevoEstado);
      response.put("success", true);
      response.put("message", "Estado actualizado exitosamente");
      response.put("hackaton", hackatonActualizado);
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      response.put("success", false);
      response.put("message", "Estado inválido o hackatón no encontrado");
      return ResponseEntity.badRequest().body(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al cambiar el estado: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Obtener estadísticas de hackatones
   */
  @GetMapping("/estadisticas")
  public ResponseEntity<?> obtenerEstadisticas() {
    Map<String, Object> estadisticas = new HashMap<>();

    estadisticas.put("total", hackatonService.listarTodos().size());
    estadisticas.put("proximos", hackatonService.contarPorEstado(HackatonModel.Estado.PROXIMO));
    estadisticas.put("enCurso", hackatonService.contarPorEstado(HackatonModel.Estado.EN_CURSO));
    estadisticas.put("finalizados", hackatonService.contarPorEstado(HackatonModel.Estado.FINALIZADO));

    return ResponseEntity.ok(estadisticas);
  }

  // ==================== ENDPOINTS DE CATEGORÍAS ====================

  /**
   * Obtener todas las categorías
   */
  @GetMapping("/categorias")
  public ResponseEntity<List<CategoriaDTO>> listarCategorias() {
    List<CategoriaDTO> categorias = categoriaService.listarTodas();
    return ResponseEntity.ok(categorias);
  }

  /**
   * Crear una nueva categoría
   */
  @PostMapping("/categorias")
  public ResponseEntity<?> crearCategoria(@Valid @RequestBody CategoriaDTO categoriaDTO, BindingResult bindingResult) {
    Map<String, Object> response = new HashMap<>();

    if (bindingResult.hasErrors()) {
      Map<String, String> errors = bindingResult.getFieldErrors().stream()
          .collect(Collectors.toMap(
              error -> error.getField(),
              error -> error.getDefaultMessage()));
      response.put("success", false);
      response.put("errors", errors);
      return ResponseEntity.badRequest().body(response);
    }

    try {
      CategoriaDTO categoriaCreada = categoriaService.crearCategoria(categoriaDTO);
      response.put("success", true);
      response.put("message", "Categoría creada exitosamente");
      response.put("categoria", categoriaCreada);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
      response.put("success", false);
      response.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }
}
