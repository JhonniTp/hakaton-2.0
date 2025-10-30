package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.UsuarioSimpleDTO;
import com.hakaton.hakaton.model.JuradoHackatonModel;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.repository.JuradoHackatonRepository;
import com.hakaton.hakaton.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador para gestionar usuarios, roles y asignación de jurados
 */
@RestController
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminUsuarioController {

  private final UsuarioRepository usuarioRepository;
  private final JuradoHackatonRepository juradoHackatonRepository;
  private final HackatonRepository hackatonRepository;

  public AdminUsuarioController(
      UsuarioRepository usuarioRepository,
      JuradoHackatonRepository juradoHackatonRepository,
      HackatonRepository hackatonRepository) {
    this.usuarioRepository = usuarioRepository;
    this.juradoHackatonRepository = juradoHackatonRepository;
    this.hackatonRepository = hackatonRepository;
  }

  /**
   * Obtener todos los usuarios
   */
  @GetMapping
  public ResponseEntity<List<UsuarioSimpleDTO>> listarTodos() {
    List<UsuarioSimpleDTO> usuarios = usuarioRepository.findAll()
        .stream()
        .map(this::convertirADTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(usuarios);
  }

  /**
   * Obtener solo usuarios con rol JURADO
   */
  @GetMapping("/jurados")
  public ResponseEntity<List<UsuarioSimpleDTO>> listarJurados() {
    List<UsuarioSimpleDTO> jurados = usuarioRepository.findByRol(UsuarioModel.Rol.JURADO)
        .stream()
        .map(this::convertirADTO)
        .collect(Collectors.toList());
    return ResponseEntity.ok(jurados);
  }

  /**
   * Cambiar el rol de un usuario
   */
  @PatchMapping("/{id}/rol")
  @Transactional
  public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> payload) {
    Map<String, Object> response = new HashMap<>();

    try {
      UsuarioModel usuario = usuarioRepository.findById(id)
          .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

      String nuevoRolStr = payload.get("rol");
      if (nuevoRolStr == null || nuevoRolStr.trim().isEmpty()) {
        response.put("success", false);
        response.put("message", "El rol es obligatorio");
        return ResponseEntity.badRequest().body(response);
      }

      UsuarioModel.Rol nuevoRol;
      try {
        nuevoRol = UsuarioModel.Rol.valueOf(nuevoRolStr.toUpperCase());
      } catch (IllegalArgumentException e) {
        response.put("success", false);
        response.put("message", "Rol inválido. Use: ADMINISTRADOR, JURADO o PARTICIPANTE");
        return ResponseEntity.badRequest().body(response);
      }

      usuario.setRol(nuevoRol);
      usuarioRepository.save(usuario);

      response.put("success", true);
      response.put("message", "Rol actualizado exitosamente");
      response.put("usuario", convertirADTO(usuario));

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al cambiar el rol: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Obtener jurados asignados a un hackatón
   */
  @GetMapping("/hackatones/{hackatonId}/jurados")
  public ResponseEntity<List<UsuarioSimpleDTO>> obtenerJuradosDeHackaton(@PathVariable Long hackatonId) {
    List<UsuarioSimpleDTO> jurados = juradoHackatonRepository.findByHackatonId(hackatonId)
        .stream()
        .map(jh -> convertirADTO(jh.getJurado()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(jurados);
  }

  /**
   * Asignar un jurado a un hackatón
   */
  @PostMapping("/hackatones/{hackatonId}/jurados/{juradoId}")
  @Transactional
  public ResponseEntity<?> asignarJurado(@PathVariable Long hackatonId, @PathVariable Long juradoId) {
    Map<String, Object> response = new HashMap<>();

    try {
      HackatonModel hackaton = hackatonRepository.findById(hackatonId)
          .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado con ID: " + hackatonId));

      UsuarioModel jurado = usuarioRepository.findById(juradoId)
          .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + juradoId));

      // Validar que el usuario tiene rol JURADO
      if (!UsuarioModel.Rol.JURADO.equals(jurado.getRol())) {
        response.put("success", false);
        response.put("message", "El usuario debe tener rol JURADO");
        return ResponseEntity.badRequest().body(response);
      }

      // Verificar si ya está asignado
      if (juradoHackatonRepository.existsByJuradoIdAndHackatonId(juradoId, hackatonId)) {
        response.put("success", false);
        response.put("message", "El jurado ya está asignado a este hackatón");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
      }

      JuradoHackatonModel asignacion = new JuradoHackatonModel(jurado, hackaton);
      juradoHackatonRepository.save(asignacion);

      response.put("success", true);
      response.put("message", "Jurado asignado exitosamente");
      response.put("asignacion", Map.of(
          "jurado", convertirADTO(jurado),
          "hackaton", hackaton.getNombre()));

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al asignar jurado: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Desasignar un jurado de un hackatón
   */
  @DeleteMapping("/hackatones/{hackatonId}/jurados/{juradoId}")
  @Transactional
  public ResponseEntity<?> desasignarJurado(@PathVariable Long hackatonId, @PathVariable Long juradoId) {
    Map<String, Object> response = new HashMap<>();

    try {
      HackatonModel hackaton = hackatonRepository.findById(hackatonId)
          .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado con ID: " + hackatonId));

      UsuarioModel jurado = usuarioRepository.findById(juradoId)
          .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + juradoId));

      if (!juradoHackatonRepository.existsByJuradoIdAndHackatonId(juradoId, hackatonId)) {
        response.put("success", false);
        response.put("message", "El jurado no está asignado a este hackatón");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      juradoHackatonRepository.deleteByJuradoAndHackaton(jurado, hackaton);

      response.put("success", true);
      response.put("message", "Jurado desasignado exitosamente");

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      response.put("success", false);
      response.put("message", "Error al desasignar jurado: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  /**
   * Convierte un UsuarioModel a UsuarioSimpleDTO
   */
  private UsuarioSimpleDTO convertirADTO(UsuarioModel usuario) {
    return new UsuarioSimpleDTO(
        usuario.getIdUsuario(),
        usuario.getNombreCompleto(),
        usuario.getCorreoElectronico(),
        usuario.getRol().name(),
        usuario.getTelefono());
  }
}
