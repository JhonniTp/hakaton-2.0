package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.*;
import com.hakaton.hakaton.service.ParticipanteDashboardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.hakaton.hakaton.model.UsuarioModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/participante")
public class ParticipanteController {

    private final ParticipanteDashboardService dashboardService;

    public ParticipanteController(ParticipanteDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(@AuthenticationPrincipal UsuarioModel usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "participante/dashboard_participantes";
    }

    @GetMapping("/api/estadisticas")
    @ResponseBody
    public ResponseEntity<?> obtenerEstadisticas(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            EstadisticasParticipanteDTO stats = dashboardService.obtenerEstadisticas(usuario.getIdUsuario());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener estadísticas: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/hackathons-activos")
    @ResponseBody
    public ResponseEntity<?> obtenerHackatonesActivos(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            List<HackathonActivoDTO> hackathons = dashboardService.obtenerHackatonesActivos(usuario.getIdUsuario());
            return ResponseEntity.ok(hackathons);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener hackathons activos: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/tareas-pendientes")
    @ResponseBody
    public ResponseEntity<?> obtenerTareasPendientes(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            List<TareaPendienteDTO> tareas = dashboardService.obtenerProximasTareas(usuario.getIdUsuario());
            return ResponseEntity.ok(tareas);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener tareas pendientes: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/logros")
    @ResponseBody
    public ResponseEntity<?> obtenerLogros(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            List<LogroDTO> logros = dashboardService.obtenerLogrosRecientes(usuario.getIdUsuario());
            return ResponseEntity.ok(logros);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener logros: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @GetMapping("/api/hackathons-disponibles")
    @ResponseBody
    public ResponseEntity<?> obtenerHackathonsDisponibles(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            List<HackatonDTO> hackathons = dashboardService.obtenerHackathonsDisponibles(usuario.getIdUsuario());
            return ResponseEntity.ok(hackathons);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener hackathons disponibles: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @GetMapping("/api/mis-equipos")
    @ResponseBody
    public ResponseEntity<?> obtenerMisEquipos(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            List<EquipoDTO> equipos = dashboardService.obtenerMisEquipos(usuario.getIdUsuario());
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener equipos: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/api/mis-proyectos")
    @ResponseBody
    public ResponseEntity<?> obtenerMisProyectos(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            List<ProyectoDTO> proyectos = dashboardService.obtenerMisProyectos(usuario.getIdUsuario());
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener proyectos: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @GetMapping("/api/mi-perfil")
    @ResponseBody
    public ResponseEntity<?> obtenerMiPerfil(@AuthenticationPrincipal UsuarioModel usuario) {
        try {
            PerfilParticipanteDTO perfil = dashboardService.obtenerPerfilCompleto(usuario.getIdUsuario());
            return ResponseEntity.ok(perfil);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener perfil: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/api/mi-perfil")
    @ResponseBody
    public ResponseEntity<?> actualizarMiPerfil(
            @AuthenticationPrincipal UsuarioModel usuario,
            @RequestBody Map<String, String> datos) {
        Map<String, Object> response = new HashMap<>();
        try {
            dashboardService.actualizarPerfil(usuario.getIdUsuario(), datos);
            response.put("success", true);
            response.put("mensaje", "Perfil actualizado exitosamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al actualizar perfil: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/ranking")
    @ResponseBody
    public ResponseEntity<?> obtenerRanking() {
        try {
            List<RankingParticipanteDTO> ranking = dashboardService.obtenerRankingGeneral();
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al obtener ranking: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @PostMapping("/api/inscribirse/{idHackaton}")
    @ResponseBody
    public ResponseEntity<?> inscribirseHackaton(
            @PathVariable Long idHackaton,
            @AuthenticationPrincipal UsuarioModel usuario) {
        try {
            Map<String, Object> response = dashboardService.inscribirseHackaton(
                    usuario.getIdUsuario(), idHackaton, usuario);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al inscribirse: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/api/desinscribirse/{idHackaton}")
    @ResponseBody
    public ResponseEntity<?> desinscribirseHackaton(
            @PathVariable Long idHackaton,
            @AuthenticationPrincipal UsuarioModel usuario) {
        try {
            Map<String, Object> response = dashboardService.desinscribirseHackaton(
                    usuario.getIdUsuario(), idHackaton);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al desinscribirse: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @PostMapping("/api/crear-equipo")
    @ResponseBody
    public ResponseEntity<?> crearEquipo(
            @RequestBody CrearEquipoRequest request,
            @AuthenticationPrincipal UsuarioModel usuario) {
        try {
            Map<String, Object> response = dashboardService.crearEquipo(
                    request, usuario.getIdUsuario(), usuario);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al crear equipo: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/api/unirse-equipo/{idEquipo}")
    @ResponseBody
    public ResponseEntity<?> unirseEquipo(
            @PathVariable Long idEquipo,
            @AuthenticationPrincipal UsuarioModel usuario) {
        try {
            Map<String, Object> response = dashboardService.unirseAEquipo(
                    usuario.getIdUsuario(), idEquipo, usuario);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al unirse al equipo: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }


    @PutMapping("/api/proyectos/{idProyecto}")
    @ResponseBody
    public ResponseEntity<?> actualizarProyecto(
            @PathVariable Long idProyecto,
            @RequestBody ActualizarProyectoRequest request,
            @AuthenticationPrincipal UsuarioModel usuario) {
        try {
            Map<String, Object> response = dashboardService.actualizarProyecto(
                    idProyecto, request, usuario.getIdUsuario());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("mensaje", "Error al actualizar proyecto: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
