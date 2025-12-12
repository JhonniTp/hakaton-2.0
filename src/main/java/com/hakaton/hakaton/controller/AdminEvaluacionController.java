package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.EvaluacionDTO;
import com.hakaton.hakaton.dto.ProyectoEvaluacionDTO;
import com.hakaton.hakaton.service.EvaluacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/evaluaciones")
public class AdminEvaluacionController {

    private final EvaluacionService evaluacionService;

    public AdminEvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @GetMapping
    public ResponseEntity<List<EvaluacionDTO>> listarTodas() {
        return ResponseEntity.ok(evaluacionService.obtenerTodas());
    }

    @GetMapping("/proyecto/{id}")
    public ResponseEntity<List<EvaluacionDTO>> obtenerPorProyecto(@PathVariable Long id) {
        return ResponseEntity.ok(evaluacionService.obtenerEvaluacionesPorProyecto(id));
    }

    @GetMapping("/proyecto/{id}/detalle")
    public ResponseEntity<ProyectoEvaluacionDTO> obtenerDetalleProyecto(@PathVariable Long id) {
        return ResponseEntity.ok(evaluacionService.obtenerDetalleEvaluacion(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody EvaluacionDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            EvaluacionDTO creada = evaluacionService.registrarEvaluacion(dto);
            response.put("success", true);
            response.put("mensaje", "Evaluación registrada exitosamente");
            response.put("data", creada);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            evaluacionService.eliminar(id);
            response.put("success", true);
            response.put("mensaje", "Evaluación eliminada exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
