package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.JuradoHackatonDTO;
import com.hakaton.hakaton.service.JuradoHackatonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/jurados-hackatones")
public class AdminJuradoHackatonController {

    @Autowired
    private JuradoHackatonService juradoHackatonService;

    @GetMapping
    public ResponseEntity<List<JuradoHackatonDTO>> obtenerTodasLasAsignaciones() {
        try {
            List<JuradoHackatonDTO> asignaciones = juradoHackatonService.obtenerTodasLasAsignaciones();
            return ResponseEntity.ok(asignaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/jurado/{idJurado}")
    public ResponseEntity<List<JuradoHackatonDTO>> obtenerHackatonesDeJurado(@PathVariable Long idJurado) {
        try {
            List<JuradoHackatonDTO> asignaciones = juradoHackatonService.obtenerHackatonesDeJurado(idJurado);
            return ResponseEntity.ok(asignaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/hackaton/{idHackaton}")
    public ResponseEntity<List<JuradoHackatonDTO>> obtenerJuradosDeHackaton(@PathVariable Long idHackaton) {
        try {
            List<JuradoHackatonDTO> asignaciones = juradoHackatonService.obtenerJuradosDeHackaton(idHackaton);
            return ResponseEntity.ok(asignaciones);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping
    public ResponseEntity<Map<String, Object>> asignarJurado(@RequestBody Map<String, Long> datos) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long idJurado = datos.get("idJurado");
            Long idHackaton = datos.get("idHackaton");

            if (idJurado == null || idHackaton == null) {
                response.put("success", false);
                response.put("mensaje", "Debe proporcionar idJurado e idHackaton");
                return ResponseEntity.badRequest().body(response);
            }

            JuradoHackatonDTO asignacion = juradoHackatonService.asignarJurado(idJurado, idHackaton);

            response.put("success", true);
            response.put("mensaje", "Jurado asignado exitosamente");
            response.put("data", asignacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al asignar jurado: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/{idJuradoHackaton}")
    public ResponseEntity<Map<String, Object>> removerAsignacion(@PathVariable Long idJuradoHackaton) {
        Map<String, Object> response = new HashMap<>();

        try {
            juradoHackatonService.removerAsignacion(idJuradoHackaton);

            response.put("success", true);
            response.put("mensaje", "Asignación removida exitosamente");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al remover asignación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/verificar")
    public ResponseEntity<Map<String, Object>> verificarAsignacion(
            @RequestParam Long idJurado,
            @RequestParam Long idHackaton) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean estaAsignado = juradoHackatonService.estaAsignado(idJurado, idHackaton);
            response.put("asignado", estaAsignado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/hackaton/{idHackaton}/contar")
    public ResponseEntity<Map<String, Object>> contarJuradosPorHackaton(@PathVariable Long idHackaton) {
        Map<String, Object> response = new HashMap<>();

        try {
            long cantidad = juradoHackatonService.contarJuradosPorHackaton(idHackaton);
            response.put("cantidad", cantidad);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/jurado/{idJurado}/contar")
    public ResponseEntity<Map<String, Object>> contarHackatonesPorJurado(@PathVariable Long idJurado) {
        Map<String, Object> response = new HashMap<>();

        try {
            long cantidad = juradoHackatonService.contarHackatonesPorJurado(idJurado);
            response.put("cantidad", cantidad);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
