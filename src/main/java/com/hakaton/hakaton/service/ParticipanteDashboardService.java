package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.*;
import com.hakaton.hakaton.model.*;
import com.hakaton.hakaton.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParticipanteDashboardService {

    private final InscripcionRepository inscripcionRepository;
    private final EquipoRepository equipoRepository;
    private final ParticipanteEquipoRepository participanteEquipoRepository;
    private final ProyectoRepository proyectoRepository;
    private final EvaluacionRepository evaluacionRepository;
    private final HackatonRepository hackatonRepository;
    private final UsuarioRepository usuarioRepository;

    public ParticipanteDashboardService(
            InscripcionRepository inscripcionRepository,
            EquipoRepository equipoRepository,
            ParticipanteEquipoRepository participanteEquipoRepository,
            ProyectoRepository proyectoRepository,
            EvaluacionRepository evaluacionRepository,
            HackatonRepository hackatonRepository,
            UsuarioRepository usuarioRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.equipoRepository = equipoRepository;
        this.participanteEquipoRepository = participanteEquipoRepository;
        this.proyectoRepository = proyectoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.hackatonRepository = hackatonRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public EstadisticasParticipanteDTO obtenerEstadisticas(Long idParticipante) {
        // Total de hackathons en los que ha participado
        int totalHackathons = inscripcionRepository.countByUsuarioIdUsuario(idParticipante);

        // Hackathons activos (estado EN_CURSO)
        List<InscripcionModel> inscripciones = inscripcionRepository.findByUsuarioIdUsuario(idParticipante);
        long hackatonesActivos = inscripciones.stream()
                .filter(i -> i.getHackaton().getEstado() == HackatonModel.Estado.EN_CURSO)
                .count();

        // Equipos del participante
        List<ParticipanteEquipoModel> participaciones = participanteEquipoRepository
                .findByUsuarioIdUsuario(idParticipante);

        // Proyectos activos y completados
        int proyectosActivos = 0;
        int proyectosCompletados = 0;
        List<Double> puntuaciones = new ArrayList<>();

        for (ParticipanteEquipoModel participacion : participaciones) {
            EquipoModel equipo = participacion.getEquipo();
            HackatonModel hackaton = equipo.getHackaton();

            // Buscar proyecto del equipo
            ProyectoModel proyecto = proyectoRepository.findByEquipoIdEquipo(equipo.getIdEquipo());

            if (proyecto != null) {
                if (hackaton.getEstado() == HackatonModel.Estado.EN_CURSO) {
                    proyectosActivos++;
                } else if (hackaton.getEstado() == HackatonModel.Estado.FINALIZADO) {
                    proyectosCompletados++;

                    // Calcular puntuación promedio del proyecto
                    List<EvaluacionModel> evaluaciones = evaluacionRepository
                            .findByProyectoIdProyecto(proyecto.getIdProyecto());
                    if (!evaluaciones.isEmpty()) {
                        double promedio = evaluaciones.stream()
                                .mapToDouble(e -> e.getPuntuacion().doubleValue())
                                .average()
                                .orElse(0.0);
                        puntuaciones.add(promedio);
                    }
                }
            }
        }

        // Puntuación promedio general
        double puntuacionPromedio = puntuaciones.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // TODO: Implementar ranking real (requiere tabla de rankings o cálculo)
        int posicionRanking = 0;
        int totalParticipantes = 0;

        // Logros obtenidos (por ahora mock, implementar en futuro)
        int logrosObtenidos = calcularLogros(proyectosCompletados, puntuacionPromedio);

        EstadisticasParticipanteDTO stats = new EstadisticasParticipanteDTO();
        stats.setTotalHackathons(totalHackathons);
        stats.setHackatonesActivos((int) hackatonesActivos);
        stats.setProyectosActivos(proyectosActivos);
        stats.setProyectosCompletados(proyectosCompletados);
        stats.setPuntuacionPromedio(Math.round(puntuacionPromedio * 10.0) / 10.0);
        stats.setPosicionRanking(posicionRanking);
        stats.setTotalParticipantes(totalParticipantes);
        stats.setLogrosObtenidos(logrosObtenidos);
        stats.setEquiposActivos((int) participaciones.stream()
                .filter(p -> p.getEquipo().getHackaton().getEstado() == HackatonModel.Estado.EN_CURSO)
                .count());

        return stats;
    }

    @Transactional(readOnly = true)
    public List<HackathonActivoDTO> obtenerHackatonesActivos(Long idParticipante) {
        List<InscripcionModel> inscripciones = inscripcionRepository.findByUsuarioIdUsuario(idParticipante);

        return inscripciones.stream()
                .filter(i -> i.getHackaton().getEstado() == HackatonModel.Estado.EN_CURSO)
                .map(inscripcion -> {
                    HackatonModel hackaton = inscripcion.getHackaton();
                    HackathonActivoDTO dto = new HackathonActivoDTO();

                    dto.setIdHackathon(hackaton.getIdHackaton());
                    dto.setNombre(hackaton.getNombre());
                    dto.setDescripcion(hackaton.getDescripcion());
                    dto.setFechaInicio(hackaton.getFechaInicio());
                    dto.setFechaFin(hackaton.getFechaFin());
                    dto.setEstado(hackaton.getEstado().toString());
                    dto.setImgUrl(null); // TODO: Add imgUrl to HackatonModel

                    // Buscar equipo del participante en este hackaton
                    List<EquipoModel> equipos = equipoRepository.findByHackatonIdHackaton(hackaton.getIdHackaton());
                    for (EquipoModel equipo : equipos) {
                        boolean esmiembro = equipo.getParticipantes().stream()
                                .anyMatch(p -> p.getUsuario().getIdUsuario().equals(idParticipante));

                        if (esmiembro) {
                            dto.setIdEquipo(equipo.getIdEquipo());
                            dto.setNombreEquipo(equipo.getNombreEquipo());
                            dto.setMiembrosEquipo(equipo.getParticipantes().size());
                            dto.setMaxMiembros(4); // TODO: Configurar límite

                            // Buscar proyecto
                            ProyectoModel proyecto = proyectoRepository.findByEquipoIdEquipo(equipo.getIdEquipo());
                            if (proyecto != null) {
                                dto.setIdProyecto(proyecto.getIdProyecto());
                                dto.setNombreProyecto(proyecto.getNombreProyecto());
                                dto.setFechaEntrega(proyecto.getFechaEntrega());

                                // Calcular progreso (si tiene entregable = 100%, sino calcular)
                                int progreso = proyecto.getUrlEntregable() != null ? 100 : 50;
                                dto.setProgresoProyecto(progreso);
                            }

                            break;
                        }
                    }

                    // Calcular tiempo restante
                    LocalDateTime ahora = LocalDateTime.now();
                    Duration duracion = Duration.between(ahora, hackaton.getFechaFin());
                    dto.setHorasRestantes(duracion.toHours());
                    dto.setDiasRestantes(duracion.toDays());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TareaPendienteDTO> obtenerProximasTareas(Long idParticipante) {
        List<TareaPendienteDTO> tareas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        // Obtener equipos activos del participante
        List<ParticipanteEquipoModel> participaciones = participanteEquipoRepository
                .findByUsuarioIdUsuario(idParticipante);

        for (ParticipanteEquipoModel participacion : participaciones) {
            EquipoModel equipo = participacion.getEquipo();
            HackatonModel hackaton = equipo.getHackaton();

            if (hackaton.getEstado() == HackatonModel.Estado.EN_CURSO) {
                // Tarea: Entregar proyecto final
                ProyectoModel proyecto = proyectoRepository.findByEquipoIdEquipo(equipo.getIdEquipo());
                if (proyecto != null && proyecto.getUrlEntregable() == null) {
                    TareaPendienteDTO tarea = new TareaPendienteDTO();
                    tarea.setTitulo("Entregar prototipo final");
                    tarea.setDescripcion("Subir URL del proyecto completado");
                    tarea.setTipo("ENTREGA");
                    tarea.setFechaLimite(hackaton.getFechaFin());
                    tarea.setPrioridad(calcularPrioridad(hackaton.getFechaFin()));
                    tarea.setHackathon(hackaton.getNombre());
                    tarea.setIdRelacionado(proyecto.getIdProyecto());
                    tarea.setDiasRestantes((int) Duration.between(ahora, hackaton.getFechaFin()).toDays());
                    tareas.add(tarea);
                }

                // Tarea: Preparar presentación
                if (proyecto != null && proyecto.getUrlPresentacion() == null) {
                    TareaPendienteDTO tarea = new TareaPendienteDTO();
                    tarea.setTitulo("Preparar presentación");
                    tarea.setDescripcion("Crear slides para el pitch final");
                    tarea.setTipo("PRESENTACION");
                    tarea.setFechaLimite(hackaton.getFechaFin().minusDays(1));
                    tarea.setPrioridad(calcularPrioridad(hackaton.getFechaFin().minusDays(1)));
                    tarea.setHackathon(hackaton.getNombre());
                    tarea.setIdRelacionado(proyecto.getIdProyecto());
                    tarea.setDiasRestantes((int) Duration.between(ahora, hackaton.getFechaFin().minusDays(1)).toDays());
                    tareas.add(tarea);
                }
            }
        }

        // Ordenar por fecha límite
        return tareas.stream()
                .sorted((a, b) -> a.getFechaLimite().compareTo(b.getFechaLimite()))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LogroDTO> obtenerLogrosRecientes(Long idParticipante) {
        List<LogroDTO> logros = new ArrayList<>();

        // Obtener proyectos completados
        List<ParticipanteEquipoModel> participaciones = participanteEquipoRepository
                .findByUsuarioIdUsuario(idParticipante);

        for (ParticipanteEquipoModel participacion : participaciones) {
            EquipoModel equipo = participacion.getEquipo();
            HackatonModel hackaton = equipo.getHackaton();

            if (hackaton.getEstado() == HackatonModel.Estado.FINALIZADO) {
                ProyectoModel proyecto = proyectoRepository.findByEquipoIdEquipo(equipo.getIdEquipo());

                if (proyecto != null) {
                    // Calcular posición (mock por ahora)
                    List<EvaluacionModel> evaluaciones = evaluacionRepository
                            .findByProyectoIdProyecto(proyecto.getIdProyecto());

                    if (!evaluaciones.isEmpty()) {
                        double promedio = evaluaciones.stream()
                                .mapToDouble(e -> e.getPuntuacion().doubleValue())
                                .average()
                                .orElse(0.0);

                        // Crear logro si puntuación alta
                        if (promedio >= 8.0) {
                            LogroDTO logro = new LogroDTO();
                            logro.setTitulo("Excelencia Técnica");
                            logro.setDescripcion("Obtener calificación de 8+ en un proyecto");
                            logro.setIcono("fas fa-star");
                            logro.setTipo("HABILIDAD");
                            logro.setFechaObtencion(hackaton.getFechaFin());
                            logro.setHackathon(hackaton.getNombre());
                            logro.setColor("yellow");
                            logros.add(logro);
                        }
                    }

                    // Logro por ser líder
                    if (participacion.isEsLider()) {
                        LogroDTO logro = new LogroDTO();
                        logro.setTitulo("Líder de Equipo");
                        logro.setDescripcion("Liderar un equipo en un hackathon");
                        logro.setIcono("fas fa-crown");
                        logro.setTipo("RACHA");
                        logro.setFechaObtencion(equipo.getFechaCreacion());
                        logro.setHackathon(hackaton.getNombre());
                        logro.setColor("blue");
                        logros.add(logro);
                    }
                }
            }
        }

        // Retornar últimos 3 logros
        return logros.stream()
                .sorted((a, b) -> b.getFechaObtencion().compareTo(a.getFechaObtencion()))
                .limit(3)
                .collect(Collectors.toList());
    }

    // Métodos auxiliares
    private String calcularPrioridad(LocalDateTime fechaLimite) {
        long diasRestantes = Duration.between(LocalDateTime.now(), fechaLimite).toDays();
        if (diasRestantes <= 2)
            return "ALTA";
        if (diasRestantes <= 5)
            return "MEDIA";
        return "BAJA";
    }

    private int calcularLogros(int proyectosCompletados, double puntuacionPromedio) {
        int logros = 0;
        if (proyectosCompletados >= 1)
            logros++;
        if (proyectosCompletados >= 3)
            logros++;
        if (proyectosCompletados >= 5)
            logros++;
        if (puntuacionPromedio >= 8.0)
            logros++;
        if (puntuacionPromedio >= 9.0)
            logros++;
        return logros;
    }

    // ========================================
    // EXPLORAR HACKATHONS
    // ========================================

    @Transactional(readOnly = true)
    public List<HackatonDTO> obtenerHackathonsDisponibles(Long idParticipante) {
        List<HackatonModel> todosHackathons = hackatonRepository.findAll();
        List<InscripcionModel> misInscripciones = inscripcionRepository.findByUsuarioIdUsuario(idParticipante);

        List<Long> idsInscritos = misInscripciones.stream()
                .map(i -> i.getHackaton().getIdHackaton())
                .collect(Collectors.toList());

        return todosHackathons.stream()
                .filter(h -> h.getEstado() != HackatonModel.Estado.FINALIZADO)
                .map(h -> {
                    HackatonDTO dto = new HackatonDTO(h);
                    dto.setEstaInscrito(idsInscritos.contains(h.getIdHackaton()));

                    // Contar participantes actuales
                    int participantesActuales = inscripcionRepository
                            .findByHackatonIdHackaton(h.getIdHackaton()).size();
                    dto.setParticipantesActuales(participantesActuales);
                    dto.setLugaresDisponibles(h.getMaximoParticipantes() - participantesActuales);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ========================================
    // MIS EQUIPOS
    // ========================================

    @Transactional(readOnly = true)
    public List<EquipoDTO> obtenerMisEquipos(Long idParticipante) {
        List<ParticipanteEquipoModel> participaciones = participanteEquipoRepository
                .findByUsuarioIdUsuario(idParticipante);

        return participaciones.stream()
                .map(p -> {
                    EquipoModel equipo = p.getEquipo();
                    EquipoDTO dto = new EquipoDTO(equipo);

                    // Buscar líder
                    participanteEquipoRepository.findLiderByEquipoId(equipo.getIdEquipo())
                            .ifPresent(lider -> dto.setNombreLider(
                                    lider.getUsuario().getNombre() + " " + lider.getUsuario().getApellido()));

                    // Estado del hackathon
                    dto.setEstadoHackathon(equipo.getHackaton().getEstado().toString());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ========================================
    // MIS PROYECTOS
    // ========================================

    @Transactional(readOnly = true)
    public List<ProyectoDTO> obtenerMisProyectos(Long idParticipante) {
        List<ParticipanteEquipoModel> participaciones = participanteEquipoRepository
                .findByUsuarioIdUsuario(idParticipante);

        List<ProyectoDTO> proyectos = new ArrayList<>();

        for (ParticipanteEquipoModel participacion : participaciones) {
            EquipoModel equipo = participacion.getEquipo();
            ProyectoModel proyecto = proyectoRepository.findByEquipoIdEquipo(equipo.getIdEquipo());

            if (proyecto != null) {
                ProyectoDTO dto = new ProyectoDTO();
                dto.setIdProyecto(proyecto.getIdProyecto());
                dto.setNombreProyecto(proyecto.getNombreProyecto());
                dto.setDescripcion(proyecto.getDescripcion());
                dto.setIdEquipo(equipo.getIdEquipo());
                dto.setNombreEquipo(equipo.getNombreEquipo());
                dto.setNombreHackathon(equipo.getHackaton().getNombre());
                dto.setUrlEntregable(proyecto.getUrlEntregable());
                dto.setUrlPresentacion(proyecto.getUrlPresentacion());
                dto.setFechaEntrega(proyecto.getFechaEntrega());

                // Calcular puntuación promedio
                List<EvaluacionModel> evaluaciones = evaluacionRepository
                        .findByProyectoIdProyecto(proyecto.getIdProyecto());
                if (!evaluaciones.isEmpty()) {
                    double promedio = evaluaciones.stream()
                            .mapToDouble(e -> e.getPuntuacion().doubleValue())
                            .average()
                            .orElse(0.0);
                    dto.setPuntuacionPromedio(promedio);
                    dto.setEstado("EVALUADO");
                } else if (proyecto.getUrlEntregable() != null) {
                    dto.setEstado("ENTREGADO");
                } else {
                    dto.setEstado("EN_PROGRESO");
                }

                proyectos.add(dto);
            }
        }

        return proyectos;
    }

    // ========================================
    // MI PERFIL
    // ========================================

    @Transactional(readOnly = true)
    public PerfilParticipanteDTO obtenerPerfilCompleto(Long idParticipante) {
        UsuarioModel usuario = participanteEquipoRepository.findByUsuarioIdUsuario(idParticipante)
                .stream()
                .findFirst()
                .map(p -> p.getUsuario())
                .orElse(null);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        PerfilParticipanteDTO perfil = new PerfilParticipanteDTO();
        perfil.setIdUsuario(usuario.getIdUsuario());
        perfil.setNombre(usuario.getNombre());
        perfil.setApellido(usuario.getApellido());
        perfil.setCorreoElectronico(usuario.getCorreoElectronico());
        perfil.setTelefono(usuario.getTelefono());
        perfil.setIniciales(usuario.getIniciales());
        perfil.setRol(usuario.getRol().toString().toLowerCase());

        // Carrera y ciclo - obtener del perfil de experiencia si está disponible
        String perfilExp = usuario.getPerfilExperiencia();
        perfil.setCarrera(perfilExp != null && !perfilExp.isBlank() ? perfilExp : "No especificado");
        perfil.setCiclo(""); // Dejar vacío si no hay información

        // Obtener estadísticas
        EstadisticasParticipanteDTO stats = obtenerEstadisticas(idParticipante);
        perfil.setTotalHackathons(stats.getTotalHackathons());
        perfil.setProyectosCompletados(stats.getProyectosCompletados());
        perfil.setPuntuacionPromedio(stats.getPuntuacionPromedio());
        perfil.setLogrosObtenidos(stats.getLogrosObtenidos());
        perfil.setPosicionRanking(stats.getPosicionRanking());

        // Biografía y habilidades
        perfil.setBiografia(usuario.getPerfilExperiencia() != null && !usuario.getPerfilExperiencia().isBlank()
                ? usuario.getPerfilExperiencia()
                : "Sin biografía");
        perfil.setHabilidades(List.of()); // Vacío por defecto, se puede agregar tabla de habilidades después

        return perfil;
    }

    @Transactional
    public void actualizarPerfil(Long idParticipante, Map<String, String> datos) {
        UsuarioModel usuario = usuarioRepository.findById(idParticipante)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos básicos
        if (datos.containsKey("nombre") && datos.get("nombre") != null && !datos.get("nombre").trim().isEmpty()) {
            usuario.setNombre(datos.get("nombre").trim());
        }

        if (datos.containsKey("apellido") && datos.get("apellido") != null && !datos.get("apellido").trim().isEmpty()) {
            usuario.setApellido(datos.get("apellido").trim());
        }

        // Manejar telefono: guardar null si está vacío para evitar conflictos con
        // UNIQUE constraint
        if (datos.containsKey("telefono")) {
            String telefono = datos.get("telefono");
            if (telefono != null && !telefono.trim().isEmpty()) {
                usuario.setTelefono(telefono.trim());
            } else {
                usuario.setTelefono(null);
            }
        }

        // Construir perfil de experiencia con carrera, ciclo y biografía
        StringBuilder perfilExp = new StringBuilder();

        if (datos.containsKey("carrera") && datos.get("carrera") != null && !datos.get("carrera").trim().isEmpty()) {
            perfilExp.append("Carrera: ").append(datos.get("carrera").trim());
        }

        if (datos.containsKey("ciclo") && datos.get("ciclo") != null && !datos.get("ciclo").trim().isEmpty()) {
            if (perfilExp.length() > 0)
                perfilExp.append(" | ");
            perfilExp.append("Ciclo: ").append(datos.get("ciclo").trim());
        }

        if (datos.containsKey("biografia") && datos.get("biografia") != null
                && !datos.get("biografia").trim().isEmpty()) {
            if (perfilExp.length() > 0)
                perfilExp.append(" | ");
            perfilExp.append(datos.get("biografia").trim());
        }

        if (perfilExp.length() > 0) {
            usuario.setPerfilExperiencia(perfilExp.toString());
        }

        // Las iniciales se calculan automáticamente en el getter basándose en nombre y
        // apellido
        usuarioRepository.save(usuario);
    }

    // ========================================
    // RANKING
    // ========================================

    @Transactional(readOnly = true)
    public List<RankingParticipanteDTO> obtenerRankingGeneral() {
        // Obtener todos los participantes únicos
        List<ParticipanteEquipoModel> todasParticipaciones = participanteEquipoRepository.findAll();

        Map<Long, RankingParticipanteDTO> rankingMap = new java.util.HashMap<>();

        for (ParticipanteEquipoModel participacion : todasParticipaciones) {
            Long idUsuario = participacion.getUsuario().getIdUsuario();

            if (!rankingMap.containsKey(idUsuario)) {
                UsuarioModel usuario = participacion.getUsuario();
                EstadisticasParticipanteDTO stats = obtenerEstadisticas(idUsuario);

                RankingParticipanteDTO dto = new RankingParticipanteDTO();
                dto.setIdUsuario(idUsuario);
                dto.setNombreCompleto(usuario.getNombre() + " " + usuario.getApellido());
                dto.setCarrera("Ingeniería de Sistemas"); // TODO: Agregar campo a UsuarioModel
                dto.setHackathonesParticipados(stats.getTotalHackathons());
                dto.setProyectosCompletados(stats.getProyectosCompletados());
                dto.setPuntuacionPromedio(stats.getPuntuacionPromedio());
                dto.setLogrosObtenidos(stats.getLogrosObtenidos());

                rankingMap.put(idUsuario, dto);
            }
        }

        // Ordenar por puntuación y asignar posiciones
        List<RankingParticipanteDTO> ranking = new ArrayList<>(rankingMap.values());
        ranking.sort((a, b) -> {
            int cmp = Double.compare(b.getPuntuacionPromedio(), a.getPuntuacionPromedio());
            if (cmp == 0) {
                cmp = Integer.compare(b.getProyectosCompletados(), a.getProyectosCompletados());
            }
            return cmp;
        });

        for (int i = 0; i < ranking.size(); i++) {
            ranking.get(i).setPosicion(i + 1);
        }

        return ranking.stream().limit(50).collect(Collectors.toList());
    }

    // ========================================
    // GESTIÓN DE INSCRIPCIONES
    // ========================================

    @Transactional
    public Map<String, Object> inscribirseHackaton(Long idUsuario, Long idHackaton, UsuarioModel usuario) {
        Map<String, Object> response = new java.util.HashMap<>();

        try {
            // Verificar si el hackathon existe
            HackatonModel hackaton = hackatonRepository.findById(idHackaton)
                    .orElseThrow(() -> new RuntimeException("Hackathon no encontrado"));

            // Verificar estado del hackathon
            if (hackaton.getEstado() == HackatonModel.Estado.FINALIZADO) {
                response.put("success", false);
                response.put("mensaje", "El hackathon ya finalizó");
                return response;
            }

            // Verificar si ya está inscrito
            boolean yaInscrito = inscripcionRepository.findByUsuarioIdUsuario(idUsuario).stream()
                    .anyMatch(i -> i.getHackaton().getIdHackaton().equals(idHackaton));

            if (yaInscrito) {
                response.put("success", false);
                response.put("mensaje", "Ya estás inscrito en este hackathon");
                return response;
            }

            // Verificar cupos disponibles
            int participantesActuales = inscripcionRepository.countByHackatonIdHackaton(idHackaton);
            if (participantesActuales >= hackaton.getMaximoParticipantes()) {
                response.put("success", false);
                response.put("mensaje", "No hay cupos disponibles");
                return response;
            }

            // Crear inscripción
            InscripcionModel inscripcion = new InscripcionModel(usuario, hackaton);
            inscripcionRepository.save(inscripcion);

            response.put("success", true);
            response.put("mensaje", "Inscripción exitosa");
            response.put("idInscripcion", inscripcion.getIdInscripcion());

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al inscribirse: " + e.getMessage());
        }

        return response;
    }

    @Transactional
    public Map<String, Object> desinscribirseHackaton(Long idUsuario, Long idHackaton) {
        Map<String, Object> response = new java.util.HashMap<>();

        try {
            // Buscar inscripción
            List<InscripcionModel> inscripciones = inscripcionRepository.findByUsuarioIdUsuario(idUsuario);
            InscripcionModel inscripcion = inscripciones.stream()
                    .filter(i -> i.getHackaton().getIdHackaton().equals(idHackaton))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No estás inscrito en este hackathon"));

            // Verificar si está en un equipo
            List<ParticipanteEquipoModel> equipos = participanteEquipoRepository.findByUsuarioIdUsuario(idUsuario);
            boolean tieneEquipo = equipos.stream()
                    .anyMatch(pe -> pe.getEquipo().getHackaton().getIdHackaton().equals(idHackaton));

            if (tieneEquipo) {
                response.put("success", false);
                response.put("mensaje", "Debes salir del equipo antes de desinscribirte");
                return response;
            }

            inscripcionRepository.delete(inscripcion);

            response.put("success", true);
            response.put("mensaje", "Desinscripción exitosa");

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al desinscribirse: " + e.getMessage());
        }

        return response;
    }

    // ========================================
    // GESTIÓN DE EQUIPOS
    // ========================================

    @Transactional
    public Map<String, Object> crearEquipo(CrearEquipoRequest request, Long idUsuarioCreador, UsuarioModel usuario) {
        Map<String, Object> response = new java.util.HashMap<>();

        // Verificar que el hackathon existe
        HackatonModel hackaton = hackatonRepository.findById(request.getIdHackaton()).orElse(null);
        if (hackaton == null) {
            response.put("success", false);
            response.put("mensaje", "Hackathon no encontrado");
            return response;
        }

        // Verificar que el usuario esté inscrito
        boolean estaInscrito = inscripcionRepository.findByUsuarioIdUsuario(idUsuarioCreador).stream()
                .anyMatch(i -> i.getHackaton().getIdHackaton().equals(request.getIdHackaton()));

        if (!estaInscrito) {
            response.put("success", false);
            response.put("mensaje", "Debes estar inscrito en el hackathon para crear un equipo");
            return response;
        }

        // Verificar que el usuario no tenga ya un equipo en este hackathon
        List<ParticipanteEquipoModel> equiposUsuario = participanteEquipoRepository
                .findByUsuarioIdUsuario(idUsuarioCreador);
        boolean yaEnEquipo = equiposUsuario.stream()
                .anyMatch(pe -> pe.getEquipo().getHackaton().getIdHackaton().equals(request.getIdHackaton()));

        if (yaEnEquipo) {
            response.put("success", false);
            response.put("mensaje", "Ya formas parte de un equipo en este hackathon");
            return response;
        }

        // Crear el equipo
        EquipoModel equipo = new EquipoModel(request.getNombreEquipo(), hackaton);
        equipoRepository.save(equipo);

        // Agregar al creador como líder
        ParticipanteEquipoModel lider = new ParticipanteEquipoModel(usuario, equipo, true);
        participanteEquipoRepository.save(lider);

        // Crear proyecto automáticamente
        ProyectoModel proyecto = new ProyectoModel("Proyecto " + equipo.getNombreEquipo(), equipo);
        proyectoRepository.save(proyecto);

        response.put("success", true);
        response.put("mensaje", "Equipo creado exitosamente");
        response.put("idEquipo", equipo.getIdEquipo());
        response.put("idProyecto", proyecto.getIdProyecto());

        return response;
    }

    @Transactional
    public Map<String, Object> unirseAEquipo(Long idUsuario, Long idEquipo, UsuarioModel usuario) {
        Map<String, Object> response = new java.util.HashMap<>();

        try {
            // Verificar que el equipo existe
            EquipoModel equipo = equipoRepository.findById(idEquipo)
                    .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

            HackatonModel hackaton = equipo.getHackaton();

            // Verificar que el usuario esté inscrito en el hackathon
            boolean estaInscrito = inscripcionRepository.findByUsuarioIdUsuario(idUsuario).stream()
                    .anyMatch(i -> i.getHackaton().getIdHackaton().equals(hackaton.getIdHackaton()));

            if (!estaInscrito) {
                response.put("success", false);
                response.put("mensaje", "Debes estar inscrito en el hackathon");
                return response;
            }

            // Verificar que no esté en otro equipo del mismo hackathon
            List<ParticipanteEquipoModel> equiposUsuario = participanteEquipoRepository
                    .findByUsuarioIdUsuario(idUsuario);
            boolean yaEnEquipo = equiposUsuario.stream()
                    .anyMatch(pe -> pe.getEquipo().getHackaton().getIdHackaton().equals(hackaton.getIdHackaton()));

            if (yaEnEquipo) {
                response.put("success", false);
                response.put("mensaje", "Ya formas parte de un equipo en este hackathon");
                return response;
            }

            // Verificar cupo del equipo
            int miembrosActuales = (int) participanteEquipoRepository.countByEquipoId(idEquipo);
            if (miembrosActuales >= hackaton.getGrupoCantidadParticipantes()) {
                response.put("success", false);
                response.put("mensaje", "El equipo está completo");
                return response;
            }

            // Agregar al equipo
            ParticipanteEquipoModel participante = new ParticipanteEquipoModel(usuario, equipo, false);
            participanteEquipoRepository.save(participante);

            response.put("success", true);
            response.put("mensaje", "Te uniste al equipo exitosamente");

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al unirse al equipo: " + e.getMessage());
        }

        return response;
    }

    // ========================================
    // GESTIÓN DE PROYECTOS
    // ========================================

    @Transactional
    public Map<String, Object> actualizarProyecto(Long idProyecto, ActualizarProyectoRequest request, Long idUsuario) {
        Map<String, Object> response = new java.util.HashMap<>();

        try {
            // Buscar proyecto
            ProyectoModel proyecto = proyectoRepository.findById(idProyecto)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

            // Verificar que el usuario sea miembro del equipo
            List<ParticipanteEquipoModel> miembros = participanteEquipoRepository
                    .findByEquipoIdEquipo(proyecto.getEquipo().getIdEquipo());

            ParticipanteEquipoModel miembro = miembros.stream()
                    .filter(m -> m.getUsuario().getIdUsuario().equals(idUsuario))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No eres miembro de este equipo"));

            // Verificar que sea el líder (solo el líder puede actualizar)
            if (!miembro.isEsLider()) {
                response.put("success", false);
                response.put("mensaje", "Solo el líder del equipo puede actualizar el proyecto");
                return response;
            }

            // Actualizar campos
            if (request.getNombreProyecto() != null && !request.getNombreProyecto().isBlank()) {
                proyecto.setNombreProyecto(request.getNombreProyecto());
            }

            if (request.getDescripcion() != null) {
                proyecto.setDescripcion(request.getDescripcion());
            }

            if (request.getUrlEntregable() != null && !request.getUrlEntregable().isBlank()) {
                proyecto.setUrlEntregable(request.getUrlEntregable());
                // Si es la primera vez que se sube, marcar fecha de entrega
                if (proyecto.getFechaEntrega() == null) {
                    proyecto.setFechaEntrega(LocalDateTime.now());
                }
            }

            if (request.getUrlPresentacion() != null && !request.getUrlPresentacion().isBlank()) {
                proyecto.setUrlPresentacion(request.getUrlPresentacion());
            }

            proyectoRepository.save(proyecto);

            response.put("success", true);
            response.put("mensaje", "Proyecto actualizado exitosamente");
            response.put("proyecto", convertirProyectoADTO(proyecto));

        } catch (Exception e) {
            response.put("success", false);
            response.put("mensaje", "Error al actualizar proyecto: " + e.getMessage());
        }

        return response;
    }

    private ProyectoDTO convertirProyectoADTO(ProyectoModel proyecto) {
        ProyectoDTO dto = new ProyectoDTO();
        dto.setIdProyecto(proyecto.getIdProyecto());
        dto.setNombreProyecto(proyecto.getNombreProyecto());
        dto.setDescripcion(proyecto.getDescripcion());
        dto.setIdEquipo(proyecto.getEquipo().getIdEquipo());
        dto.setNombreEquipo(proyecto.getEquipo().getNombreEquipo());
        dto.setNombreHackathon(proyecto.getEquipo().getHackaton().getNombre());
        dto.setUrlEntregable(proyecto.getUrlEntregable());
        dto.setUrlPresentacion(proyecto.getUrlPresentacion());
        dto.setFechaEntrega(proyecto.getFechaEntrega());

        // Calcular puntuación
        List<EvaluacionModel> evaluaciones = proyecto.getEvaluaciones();
        if (!evaluaciones.isEmpty()) {
            double promedio = evaluaciones.stream()
                    .mapToDouble(e -> e.getPuntuacion().doubleValue())
                    .average()
                    .orElse(0.0);
            dto.setPuntuacionPromedio(promedio);
            dto.setEstado("EVALUADO");
        } else if (proyecto.getUrlEntregable() != null && !proyecto.getUrlEntregable().isBlank()) {
            dto.setEstado("ENTREGADO");
        } else {
            dto.setEstado("EN_PROGRESO");
        }

        return dto;
    }
}
