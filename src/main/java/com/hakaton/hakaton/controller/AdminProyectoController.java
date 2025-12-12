package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.ProyectoEvaluacionDTO;
import com.hakaton.hakaton.model.ProyectoModel;
import com.hakaton.hakaton.repository.ProyectoRepository;
import com.hakaton.hakaton.service.EvaluacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/proyectos")
public class AdminProyectoController {

    private final ProyectoRepository proyectoRepository;
    private final EvaluacionService evaluacionService;

    public AdminProyectoController(ProyectoRepository proyectoRepository, EvaluacionService evaluacionService) {
        this.proyectoRepository = proyectoRepository;
        this.evaluacionService = evaluacionService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarProyectos() {
        List<ProyectoModel> proyectos = proyectoRepository.findAll();

        List<Map<String, Object>> response = proyectos.stream().map(proyecto -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idProyecto", proyecto.getIdProyecto());
            map.put("nombreProyecto", proyecto.getNombreProyecto());
            map.put("nombreEquipo", proyecto.getEquipo().getNombreEquipo());
            map.put("nombreHackaton", proyecto.getEquipo().getHackaton().getNombre());

            ProyectoEvaluacionDTO detalle = evaluacionService.obtenerDetalleEvaluacion(proyecto.getIdProyecto());
            map.put("evaluaciones_completadas", detalle.getEvaluacionesCompletadas());
            map.put("total_criterios", detalle.getEvaluacionesTotales());
            map.put("puntajeFinal", detalle.getPuntajeFinal());

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoEvaluacionDTO> obtenerDetalleProyecto(@PathVariable Long id) {
        return ResponseEntity.ok(evaluacionService.obtenerDetalleEvaluacion(id));
    }
}
