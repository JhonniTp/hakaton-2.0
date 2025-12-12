package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.EstadisticasDashboardDTO;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.EvaluacionRepository;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.repository.ProyectoRepository;
import com.hakaton.hakaton.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DashboardService {

    private final HackatonRepository hackatonRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EvaluacionRepository evaluacionRepository;

    public DashboardService(HackatonRepository hackatonRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            EvaluacionRepository evaluacionRepository) {
        this.hackatonRepository = hackatonRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    @Transactional(readOnly = true)
    public EstadisticasDashboardDTO obtenerEstadisticas() {
        EstadisticasDashboardDTO stats = new EstadisticasDashboardDTO();

        long hackatonesActivos = hackatonRepository.countByEstado(HackatonModel.Estado.EN_CURSO);
        stats.setHackatonesActivos(hackatonesActivos);

        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long incrementoHackatones = hackatonRepository.countByFechaCreacionAfter(inicioMes);
        stats.setIncrementoHackatonesEsteMes(incrementoHackatones);

        long totalParticipantes = usuarioRepository.countByRol(UsuarioModel.Rol.PARTICIPANTE);
        stats.setTotalParticipantes(totalParticipantes);

        LocalDateTime inicioSemana = LocalDateTime.now().minusWeeks(1);
        long incrementoParticipantes = usuarioRepository.countByFechaCreacionAfter(inicioSemana);
        stats.setIncrementoParticipantesEstaSemana(incrementoParticipantes);

        long totalProyectos = proyectoRepository.count();
        stats.setProyectosEntregados(totalProyectos);

        LocalDateTime inicioHoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long proyectosHoy = proyectoRepository.countByFechaEntregaAfter(inicioHoy);
        stats.setIncrementoProyectosHoy(proyectosHoy);

        long evaluacionesPendientes = evaluacionRepository
                .countEvaluacionesPendientesByEstado(HackatonModel.Estado.EN_CURSO);
        stats.setEvaluacionesPendientes(evaluacionesPendientes);

        return stats;
    }
}
