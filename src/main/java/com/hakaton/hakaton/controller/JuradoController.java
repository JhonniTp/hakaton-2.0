package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.CriterioEvaluacionDTO;
import com.hakaton.hakaton.dto.EvaluacionDTO;
import com.hakaton.hakaton.dto.JuradoEstadisticasDTO;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.service.CriterioEvaluacionService;
import com.hakaton.hakaton.service.EvaluacionService;
import com.hakaton.hakaton.service.JuradoDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/jurado")
public class JuradoController {

    private final JuradoDashboardService dashboardService;
    private final EvaluacionService evaluacionService;
    private final CriterioEvaluacionService criterioService;

    public JuradoController(JuradoDashboardService dashboardService,
            EvaluacionService evaluacionService,
            CriterioEvaluacionService criterioService) {
        this.dashboardService = dashboardService;
        this.evaluacionService = evaluacionService;
        this.criterioService = criterioService;
    }

    private Long obtenerIdUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UsuarioModel) {
            UsuarioModel usuario = (UsuarioModel) auth.getPrincipal();
            return usuario.getIdUsuario();
        }
        throw new RuntimeException("Usuario no autenticado");
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard() {
        return "jurado/dashboard_jurado";
    }

    @GetMapping("/api/estadisticas")
    @ResponseBody
    public ResponseEntity<JuradoEstadisticasDTO> obtenerEstadisticas() {
        Long idJurado = obtenerIdUsuarioAutenticado();
        return ResponseEntity.ok(dashboardService.obtenerEstadisticas(idJurado));
    }

    @GetMapping("/api/proyectos")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerProyectosAsignados() {
        Long idJurado = obtenerIdUsuarioAutenticado();
        return ResponseEntity.ok(dashboardService.obtenerProyectosAsignados(idJurado));
    }

    @GetMapping("/api/hackathons")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> obtenerHackatonesAsignados() {
        Long idJurado = obtenerIdUsuarioAutenticado();
        return ResponseEntity.ok(dashboardService.obtenerHackatonesAsignados(idJurado));
    }

    @GetMapping("/api/criterios")
    @ResponseBody
    public ResponseEntity<List<CriterioEvaluacionDTO>> obtenerCriterios() {
        return ResponseEntity.ok(criterioService.obtenerTodos());
    }

    @GetMapping("/api/evaluaciones/proyecto/{id}")
    @ResponseBody
    public ResponseEntity<List<EvaluacionDTO>> obtenerEvaluacionesProyecto(@PathVariable Long id) {
        Long idJurado = obtenerIdUsuarioAutenticado();
        List<EvaluacionDTO> todasEvaluaciones = evaluacionService.obtenerEvaluacionesPorProyecto(id);
        List<EvaluacionDTO> misEvaluaciones = todasEvaluaciones.stream()
                .filter(e -> e.getIdJurado().equals(idJurado))
                .toList();
        return ResponseEntity.ok(misEvaluaciones);
    }

    @PostMapping("/api/evaluaciones")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registrarEvaluacion(@RequestBody EvaluacionDTO dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long idJurado = obtenerIdUsuarioAutenticado();
            dto.setIdJurado(idJurado);
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

    @GetMapping("/api/perfil")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> obtenerPerfil() {
        Long idJurado = obtenerIdUsuarioAutenticado();
        Map<String, Object> perfil = dashboardService.obtenerPerfil(idJurado);
        return ResponseEntity.ok(perfil);
    }

    @PutMapping("/api/perfil")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarPerfil(@RequestBody Map<String, String> datos) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long idJurado = obtenerIdUsuarioAutenticado();
            dashboardService.actualizarPerfil(idJurado, datos);
            response.put("success", true);
            response.put("mensaje", "Perfil actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/proyecto/{id}/detalle")
    @ResponseBody
    public ResponseEntity<?> obtenerDetalleProyecto(@PathVariable Long id) {
        try {
            Long idJurado = obtenerIdUsuarioAutenticado();
            return ResponseEntity.ok(dashboardService.obtenerDetalleProyecto(id, idJurado));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/rankings/hackathon/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerRanking(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(dashboardService.obtenerRanking(id));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/cronograma")
    @ResponseBody
    public ResponseEntity<?> obtenerCronograma() {
        try {
            Long idJurado = obtenerIdUsuarioAutenticado();
            return ResponseEntity.ok(dashboardService.obtenerCronograma(idJurado));
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
