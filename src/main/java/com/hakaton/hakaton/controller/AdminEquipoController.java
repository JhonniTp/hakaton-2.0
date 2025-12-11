package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.EquipoDTO;
import com.hakaton.hakaton.dto.ParticipanteEquipoDTO;
import com.hakaton.hakaton.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/equipos")
public class AdminEquipoController {

    @Autowired
    private EquipoService equipoService;

    @GetMapping
    public ResponseEntity<List<EquipoDTO>> obtenerTodosLosEquipos() {
        try {
            List<EquipoDTO> equipos = equipoService.obtenerTodosLosEquipos();
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{idEquipo}")
    public ResponseEntity<EquipoDTO> obtenerEquipoPorId(@PathVariable Long idEquipo) {
        try {
            EquipoDTO equipo = equipoService.obtenerEquipoPorId(idEquipo);
            return ResponseEntity.ok(equipo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/hackaton/{idHackaton}")
    public ResponseEntity<List<EquipoDTO>> obtenerEquiposPorHackaton(@PathVariable Long idHackaton) {
        try {
            List<EquipoDTO> equipos = equipoService.obtenerEquiposPorHackaton(idHackaton);
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearEquipo(@RequestBody EquipoDTO equipoDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            EquipoDTO equipoCreado = equipoService.crearEquipo(equipoDTO);
            response.put("success", true);
            response.put("mensaje", "Equipo creado exitosamente");
            response.put("data", equipoCreado);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al crear equipo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping("/{idEquipo}")
    public ResponseEntity<Map<String, Object>> actualizarEquipo(
            @PathVariable Long idEquipo,
            @RequestBody EquipoDTO equipoDTO) {
        Map<String, Object> response = new HashMap<>();

        try {
            EquipoDTO equipoActualizado = equipoService.actualizarEquipo(idEquipo, equipoDTO);
            response.put("success", true);
            response.put("mensaje", "Equipo actualizado exitosamente");
            response.put("data", equipoActualizado);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al actualizar equipo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{idEquipo}")
    public ResponseEntity<Map<String, Object>> eliminarEquipo(@PathVariable Long idEquipo) {
        Map<String, Object> response = new HashMap<>();

        try {
            equipoService.eliminarEquipo(idEquipo);
            response.put("success", true);
            response.put("mensaje", "Equipo eliminado exitosamente");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al eliminar equipo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{idEquipo}/participantes")
    public ResponseEntity<List<ParticipanteEquipoDTO>> obtenerParticipantesDeEquipo(@PathVariable Long idEquipo) {
        try {
            List<ParticipanteEquipoDTO> participantes = equipoService.obtenerParticipantesDeEquipo(idEquipo);
            return ResponseEntity.ok(participantes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/{idEquipo}/participantes")
    public ResponseEntity<Map<String, Object>> agregarParticipante(
            @PathVariable Long idEquipo,
            @RequestBody Map<String, Object> datos) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long idUsuario = Long.valueOf(datos.get("idUsuario").toString());
            boolean esLider = datos.containsKey("esLider") && (boolean) datos.get("esLider");

            ParticipanteEquipoDTO participante = equipoService.agregarParticipante(idEquipo, idUsuario, esLider);

            response.put("success", true);
            response.put("mensaje", "Participante agregado exitosamente");
            response.put("data", participante);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al agregar participante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/participantes/{idParticipanteEquipo}")
    public ResponseEntity<Map<String, Object>> removerParticipante(@PathVariable Long idParticipanteEquipo) {
        Map<String, Object> response = new HashMap<>();

        try {
            equipoService.removerParticipante(idParticipanteEquipo);
            response.put("success", true);
            response.put("mensaje", "Participante removido exitosamente");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al remover participante: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{idEquipo}/lider")
    public ResponseEntity<Map<String, Object>> cambiarLider(
            @PathVariable Long idEquipo,
            @RequestBody Map<String, Long> datos) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long idUsuario = datos.get("idUsuario");
            equipoService.cambiarLider(idEquipo, idUsuario);

            response.put("success", true);
            response.put("mensaje", "Líder cambiado exitosamente");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al cambiar líder: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
