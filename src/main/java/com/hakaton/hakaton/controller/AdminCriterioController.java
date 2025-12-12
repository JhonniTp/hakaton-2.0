package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.CriterioEvaluacionDTO;
import com.hakaton.hakaton.service.CriterioEvaluacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/criterios")
public class AdminCriterioController {

    private final CriterioEvaluacionService criterioService;

    public AdminCriterioController(CriterioEvaluacionService criterioService) {
        this.criterioService = criterioService;
    }

    @GetMapping
    public ResponseEntity<List<CriterioEvaluacionDTO>> listarTodos() {
        return ResponseEntity.ok(criterioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterioEvaluacionDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(criterioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crear(@RequestBody CriterioEvaluacionDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            CriterioEvaluacionDTO creado = criterioService.crear(dto);
            response.put("success", true);
            response.put("mensaje", "Criterio creado exitosamente");
            response.put("data", creado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(@PathVariable Long id,
            @RequestBody CriterioEvaluacionDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            CriterioEvaluacionDTO actualizado = criterioService.actualizar(id, dto);
            response.put("success", true);
            response.put("mensaje", "Criterio actualizado exitosamente");
            response.put("data", actualizado);
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
            criterioService.eliminar(id);
            response.put("success", true);
            response.put("mensaje", "Criterio eliminado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/validar-pesos")
    public ResponseEntity<Map<String, Object>> validarPesos() {
        Map<String, Object> response = new HashMap<>();
        BigDecimal suma = criterioService.calcularSumaPesos();
        boolean valido = criterioService.validarSumaPesos();

        response.put("suma", suma);
        response.put("valido", valido);
        response.put("mensaje", valido ? "La suma de pesos es 100%" : "La suma de pesos debe ser 100%");

        return ResponseEntity.ok(response);
    }
}
