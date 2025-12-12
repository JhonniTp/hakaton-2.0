package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.JuradoEstadisticasDTO;
import com.hakaton.hakaton.dto.ProyectoEvaluacionDTO;
import com.hakaton.hakaton.dto.ProyectoDetalleDTO;
import com.hakaton.hakaton.dto.RankingProyectoDTO;
import com.hakaton.hakaton.dto.EventoCronogramaDTO;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.JuradoHackatonModel;
import com.hakaton.hakaton.model.ProyectoModel;
import com.hakaton.hakaton.repository.EvaluacionRepository;
import com.hakaton.hakaton.repository.JuradoHackatonRepository;
import com.hakaton.hakaton.repository.ProyectoRepository;
import com.hakaton.hakaton.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JuradoDashboardService {

        private final JuradoHackatonRepository juradoHackatonRepository;
        private final ProyectoRepository proyectoRepository;
        private final EvaluacionRepository evaluacionRepository;
        private final EvaluacionService evaluacionService;
        private final UsuarioRepository usuarioRepository;

        public JuradoDashboardService(JuradoHackatonRepository juradoHackatonRepository,
                        ProyectoRepository proyectoRepository,
                        EvaluacionRepository evaluacionRepository,
                        EvaluacionService evaluacionService,
                        UsuarioRepository usuarioRepository) {
                this.juradoHackatonRepository = juradoHackatonRepository;
                this.proyectoRepository = proyectoRepository;
                this.evaluacionRepository = evaluacionRepository;
                this.evaluacionService = evaluacionService;
                this.usuarioRepository = usuarioRepository;
        }

        @Transactional(readOnly = true)
        public JuradoEstadisticasDTO obtenerEstadisticas(Long idJurado) {
                List<JuradoHackatonModel> asignaciones = juradoHackatonRepository.findByJuradoId(idJurado);

                List<Long> hackatonIds = asignaciones.stream()
                                .map(j -> j.getHackaton().getIdHackaton())
                                .collect(Collectors.toList());

                int totalProyectos = 0;
                int proyectosEvaluados = 0;

                for (Long hackatonId : hackatonIds) {
                        List<ProyectoModel> proyectos = proyectoRepository.findByEquipoHackatonIdHackaton(hackatonId);
                        totalProyectos += proyectos.size();

                        for (ProyectoModel proyecto : proyectos) {
                                long evaluacionesCount = evaluacionRepository
                                                .findByProyectoIdProyecto(proyecto.getIdProyecto())
                                                .stream()
                                                .filter(e -> e.getJurado().getIdUsuario().equals(idJurado))
                                                .count();

                                if (evaluacionesCount > 0) {
                                        proyectosEvaluados++;
                                }
                        }
                }

                long hackatonesActivos = asignaciones.stream()
                                .filter(j -> j.getHackaton().getEstado() == HackatonModel.Estado.EN_CURSO)
                                .count();

                LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
                long evaluacionesMes = evaluacionRepository.findByJuradoIdUsuario(idJurado).stream()
                                .filter(e -> e.getFechaEvaluacion() != null
                                                && e.getFechaEvaluacion().isAfter(inicioMes))
                                .count();

                JuradoEstadisticasDTO stats = new JuradoEstadisticasDTO();
                stats.setProyectosAsignados(totalProyectos);
                stats.setProyectosEvaluados(proyectosEvaluados);
                stats.setProyectosPendientes(totalProyectos - proyectosEvaluados);
                stats.setHackatonesActivos((int) hackatonesActivos);
                stats.setEvaluacionesMes((int) evaluacionesMes);
                stats.setPromedioCalificacion(0.0);

                return stats;
        }

        @Transactional(readOnly = true)
        public List<Map<String, Object>> obtenerProyectosAsignados(Long idJurado) {
                List<JuradoHackatonModel> asignaciones = juradoHackatonRepository.findByJuradoId(idJurado);

                List<Long> hackatonIds = asignaciones.stream()
                                .map(j -> j.getHackaton().getIdHackaton())
                                .collect(Collectors.toList());

                return hackatonIds.stream()
                                .flatMap(hackatonId -> proyectoRepository.findByEquipoHackatonIdHackaton(hackatonId)
                                                .stream())
                                .map(proyecto -> {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("idProyecto", proyecto.getIdProyecto());
                                        map.put("nombreProyecto", proyecto.getNombreProyecto());
                                        map.put("nombreEquipo", proyecto.getEquipo().getNombreEquipo());
                                        map.put("nombreHackaton", proyecto.getEquipo().getHackaton().getNombre());
                                        map.put("descripcion", proyecto.getDescripcion());
                                        map.put("urlEntregable", proyecto.getUrlEntregable());

                                        ProyectoEvaluacionDTO detalle = evaluacionService
                                                        .obtenerDetalleEvaluacion(proyecto.getIdProyecto());

                                        long misEvaluaciones = detalle.getEvaluaciones().stream()
                                                        .filter(e -> e.getIdJurado().equals(idJurado))
                                                        .count();

                                        map.put("evaluaciones_completadas", (int) misEvaluaciones);
                                        map.put("total_criterios", detalle.getEvaluacionesTotales());
                                        map.put("estado", misEvaluaciones == 0 ? "PENDIENTE"
                                                        : (misEvaluaciones < detalle.getEvaluacionesTotales()
                                                                        ? "EN_PROGRESO"
                                                                        : "COMPLETADO"));

                                        return map;
                                })
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<Map<String, Object>> obtenerHackatonesAsignados(Long idJurado) {
                return juradoHackatonRepository.findByJuradoId(idJurado).stream()
                                .map(asignacion -> {
                                        Map<String, Object> map = new HashMap<>();
                                        HackatonModel hackaton = asignacion.getHackaton();

                                        map.put("idHackaton", hackaton.getIdHackaton());
                                        map.put("nombre", hackaton.getNombre());
                                        map.put("estado", hackaton.getEstado().toString());
                                        map.put("fechaInicio", hackaton.getFechaInicio());
                                        map.put("fechaFin", hackaton.getFechaFin());

                                        List<ProyectoModel> proyectos = proyectoRepository
                                                        .findByEquipoHackatonIdHackaton(hackaton.getIdHackaton());
                                        long evaluados = proyectos.stream()
                                                        .filter(p -> evaluacionRepository
                                                                        .findByProyectoIdProyecto(p.getIdProyecto())
                                                                        .stream()
                                                                        .anyMatch(e -> e.getJurado().getIdUsuario()
                                                                                        .equals(idJurado)))
                                                        .count();

                                        map.put("totalProyectos", proyectos.size());
                                        map.put("proyectosEvaluados", (int) evaluados);

                                        return map;
                                })
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public Map<String, Object> obtenerPerfil(Long idJurado) {
                var usuario = usuarioRepository.findById(idJurado)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                Map<String, Object> perfil = new HashMap<>();
                perfil.put("idUsuario", usuario.getIdUsuario());
                perfil.put("nombre", usuario.getNombre());
                perfil.put("apellido", usuario.getApellido());
                perfil.put("nombreCompleto", usuario.getNombreCompleto());
                perfil.put("correoElectronico", usuario.getCorreoElectronico());
                perfil.put("telefono", usuario.getTelefono() == null ? "" : usuario.getTelefono());
                perfil.put("documentoDni", usuario.getDocumentoDni() == null ? "" : usuario.getDocumentoDni());
                perfil.put("perfilExperiencia",
                                usuario.getPerfilExperiencia() == null ? "" : usuario.getPerfilExperiencia());
                perfil.put("rol", usuario.getRol().toString());
                perfil.put("iniciales", usuario.getIniciales());

                JuradoEstadisticasDTO stats = obtenerEstadisticas(idJurado);
                perfil.put("estadisticas", stats);

                return perfil;
        }

        @Transactional
        public void actualizarPerfil(Long idJurado, Map<String, String> datos) {
                var usuario = usuarioRepository.findById(idJurado)
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                if (datos.containsKey("nombre")) {
                        usuario.setNombre(datos.get("nombre"));
                }
                if (datos.containsKey("apellido")) {
                        usuario.setApellido(datos.get("apellido"));
                }
                if (datos.containsKey("telefono")) {
                        usuario.setTelefono(datos.get("telefono"));
                }
                if (datos.containsKey("perfilExperiencia")) {
                        usuario.setPerfilExperiencia(datos.get("perfilExperiencia"));
                }

                usuarioRepository.save(usuario);
        }

        @Transactional(readOnly = true)
        public ProyectoDetalleDTO obtenerDetalleProyecto(Long idProyecto, Long idJurado) {
                var proyecto = proyectoRepository.findById(idProyecto)
                                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

                ProyectoDetalleDTO detalle = new ProyectoDetalleDTO();
                detalle.setIdProyecto(proyecto.getIdProyecto());
                detalle.setNombreProyecto(proyecto.getNombreProyecto());
                detalle.setDescripcion(proyecto.getDescripcion());
                detalle.setUrlEntregable(proyecto.getUrlEntregable());
                detalle.setUrlRepositorio(proyecto.getUrlPresentacion()); // Using urlPresentacion as repository
                detalle.setNombreEquipo(proyecto.getEquipo().getNombreEquipo());
                detalle.setIdHackaton(proyecto.getEquipo().getHackaton().getIdHackaton());
                detalle.setNombreHackaton(proyecto.getEquipo().getHackaton().getNombre());

                List<String> miembros = proyecto.getEquipo().getParticipantes().stream()
                                .map(p -> p.getUsuario().getNombreCompleto()) // Using getUsuario() instead of
                                                                              // getParticipante()
                                .collect(Collectors.toList());
                detalle.setMiembros(miembros);

                ProyectoEvaluacionDTO evalDetalle = evaluacionService.obtenerDetalleEvaluacion(idProyecto);
                detalle.setEvaluacionesTotales(evalDetalle.getEvaluacionesTotales());

                long misEvaluaciones = evalDetalle.getEvaluaciones().stream()
                                .filter(e -> e.getIdJurado().equals(idJurado))
                                .count();
                detalle.setEvaluacionesCompletadas((int) misEvaluaciones);
                detalle.setYaEvalueEsteProyecto(misEvaluaciones > 0);

                detalle.setEstado(misEvaluaciones == 0 ? "PENDIENTE"
                                : (misEvaluaciones < evalDetalle.getEvaluacionesTotales() ? "EN_PROGRESO"
                                                : "COMPLETADO"));
                detalle.setPuntajePromedio(evalDetalle.getPuntajeFinal().doubleValue()); // Convert BigDecimal to Double

                return detalle;
        }

        @Transactional(readOnly = true)
        public List<RankingProyectoDTO> obtenerRanking(Long idHackaton) {
                List<ProyectoModel> proyectos = proyectoRepository.findByEquipoHackatonIdHackaton(idHackaton);

                List<RankingProyectoDTO> ranking = proyectos.stream()
                                .map(proyecto -> {
                                        ProyectoEvaluacionDTO detalle = evaluacionService
                                                        .obtenerDetalleEvaluacion(proyecto.getIdProyecto());

                                        RankingProyectoDTO rank = new RankingProyectoDTO();
                                        rank.setIdProyecto(proyecto.getIdProyecto());
                                        rank.setNombreProyecto(proyecto.getNombreProyecto());
                                        rank.setNombreEquipo(proyecto.getEquipo().getNombreEquipo());
                                        rank.setPuntajeFinal(detalle.getPuntajeFinal().doubleValue());
                                        rank.setTotalEvaluaciones(detalle.getEvaluacionesTotales());
                                        rank.setEvaluacionesCompletadas(detalle.getEvaluacionesCompletadas());
                                        rank.setEstado(detalle.getEvaluacionesCompletadas() >= detalle
                                                        .getEvaluacionesTotales() ? "COMPLETADO" : "PENDIENTE");

                                        return rank;
                                })
                                .sorted((a, b) -> Double.compare(b.getPuntajeFinal(), a.getPuntajeFinal()))
                                .collect(Collectors.toList());

                for (int i = 0; i < ranking.size(); i++) {
                        ranking.get(i).setPosicion(i + 1);
                }

                return ranking;
        }

        @Transactional(readOnly = true)
        public List<EventoCronogramaDTO> obtenerCronograma(Long idJurado) {
                List<JuradoHackatonModel> asignaciones = juradoHackatonRepository.findByJuradoId(idJurado);
                List<EventoCronogramaDTO> eventos = new ArrayList<>();

                for (JuradoHackatonModel asignacion : asignaciones) {
                        HackatonModel hackaton = asignacion.getHackaton();

                        EventoCronogramaDTO inicio = new EventoCronogramaDTO();
                        inicio.setTipo("INICIO_HACKATHON");
                        inicio.setHackaton(hackaton.getNombre());
                        inicio.setFecha(hackaton.getFechaInicio());
                        inicio.setDescripcion("Inicio del hackathon");
                        inicio.setPrioridad(hackaton.getFechaInicio().isAfter(LocalDateTime.now()) ? "ALTA" : "BAJA");
                        eventos.add(inicio);

                        EventoCronogramaDTO fin = new EventoCronogramaDTO();
                        fin.setTipo("FIN_HACKATHON");
                        fin.setHackaton(hackaton.getNombre());
                        fin.setFecha(hackaton.getFechaFin());
                        fin.setDescripcion("Cierre del hackathon y entrega de proyectos");
                        fin.setPrioridad(hackaton.getFechaFin().isAfter(LocalDateTime.now()) ? "ALTA" : "BAJA");
                        eventos.add(fin);
                }

                return eventos.stream()
                                .sorted((a, b) -> a.getFecha().compareTo(b.getFecha()))
                                .collect(Collectors.toList());
        }
}
