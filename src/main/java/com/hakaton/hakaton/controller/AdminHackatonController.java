package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.HackatonDTO;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.service.HackatonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/hackatones")
public class AdminHackatonController {

    private final HackatonService hackatonService;

    public AdminHackatonController(HackatonService hackatonService) {
        this.hackatonService = hackatonService;
    }

    @GetMapping
    public ResponseEntity<List<HackatonDTO>> obtenerTodosLosHackatones() {
        List<HackatonDTO> hackatones = hackatonService.obtenerTodosLosHackatones()
                .stream()
                .map(HackatonDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(hackatones);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerHackatonPorId(@PathVariable Long id) {
        try {
            HackatonModel hackaton = hackatonService.obtenerHackatonPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado"));
            return ResponseEntity.ok(new HackatonDTO(hackaton));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearHackaton(@Valid @RequestBody HackatonDTO hackatonDTO) {
        try {
            HackatonModel nuevoHackaton = hackatonService.crearHackaton(hackatonDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(crearRespuestaExito("Hackatón creado exitosamente", new HackatonDTO(nuevoHackaton)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al crear el hackatón: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarHackaton(@PathVariable Long id,
            @Valid @RequestBody HackatonDTO hackatonDTO) {
        try {
            HackatonModel hackatonActualizado = hackatonService.actualizarHackaton(id, hackatonDTO);
            return ResponseEntity.ok(crearRespuestaExito("Hackatón actualizado exitosamente",
                    new HackatonDTO(hackatonActualizado)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al actualizar el hackatón: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarHackaton(@PathVariable Long id) {
        try {
            hackatonService.eliminarHackaton(id);
            return ResponseEntity.ok(crearRespuestaExito("Hackatón eliminado exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al eliminar el hackatón: " + e.getMessage()));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HackatonDTO>> obtenerHackatonesPorEstado(@PathVariable String estado) {
        try {
            HackatonModel.Estado estadoEnum = HackatonModel.Estado.valueOf(estado.toUpperCase());
            List<HackatonDTO> hackatones = hackatonService.obtenerHackatonesPorEstado(estadoEnum)
                    .stream()
                    .map(HackatonDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(hackatones);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<HackatonDTO>> buscarHackatones(@RequestParam String nombre) {
        List<HackatonDTO> hackatones = hackatonService.buscarHackatonesPorNombre(nombre)
                .stream()
                .map(HackatonDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(hackatones);
    }

    @GetMapping("/verificar-nombre")
    public ResponseEntity<Map<String, Boolean>> verificarNombre(@RequestParam String nombre) {
        boolean existe = hackatonService.existeNombre(nombre);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("existe", existe);
        return ResponseEntity.ok(respuesta);
    }

    private Map<String, Object> crearRespuestaExito(String mensaje, Object data) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", mensaje);
        if (data != null) {
            respuesta.put("data", data);
        }
        return respuesta;
    }

    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", false);
        respuesta.put("mensaje", mensaje);
        return respuesta;
    }
}
