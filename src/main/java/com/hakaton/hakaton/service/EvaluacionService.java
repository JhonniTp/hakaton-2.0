package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.EvaluacionDTO;
import com.hakaton.hakaton.dto.ProyectoEvaluacionDTO;
import com.hakaton.hakaton.model.CriterioEvaluacionModel;
import com.hakaton.hakaton.model.EvaluacionModel;
import com.hakaton.hakaton.model.ProyectoModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.CriterioEvaluacionRepository;
import com.hakaton.hakaton.repository.EvaluacionRepository;
import com.hakaton.hakaton.repository.ProyectoRepository;
import com.hakaton.hakaton.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final ProyectoRepository proyectoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CriterioEvaluacionRepository criterioRepository;

    public EvaluacionService(EvaluacionRepository evaluacionRepository,
            ProyectoRepository proyectoRepository,
            UsuarioRepository usuarioRepository,
            CriterioEvaluacionRepository criterioRepository) {
        this.evaluacionRepository = evaluacionRepository;
        this.proyectoRepository = proyectoRepository;
        this.usuarioRepository = usuarioRepository;
        this.criterioRepository = criterioRepository;
    }

    @Transactional
    public EvaluacionDTO registrarEvaluacion(EvaluacionDTO dto) {
        boolean existe = evaluacionRepository.existsByProyectoIdProyectoAndCriterioIdCriterioAndJuradoIdUsuario(
                dto.getIdProyecto(), dto.getIdCriterio(), dto.getIdJurado());

        if (existe) {
            throw new RuntimeException("El jurado ya evaluó este proyecto en este criterio");
        }

        ProyectoModel proyecto = proyectoRepository.findById(dto.getIdProyecto())
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        UsuarioModel jurado = usuarioRepository.findById(dto.getIdJurado())
                .orElseThrow(() -> new RuntimeException("Jurado no encontrado"));

        CriterioEvaluacionModel criterio = criterioRepository.findById(dto.getIdCriterio())
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

        EvaluacionModel evaluacion = new EvaluacionModel();
        evaluacion.setPuntuacion(dto.getPuntuacion());
        evaluacion.setFeedback(dto.getFeedback());
        evaluacion.setProyecto(proyecto);
        evaluacion.setJurado(jurado);
        evaluacion.setCriterio(criterio);

        EvaluacionModel guardada = evaluacionRepository.save(evaluacion);
        return convertirADTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<EvaluacionDTO> obtenerEvaluacionesPorProyecto(Long idProyecto) {
        return evaluacionRepository.findByProyectoIdProyecto(idProyecto).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProyectoEvaluacionDTO obtenerDetalleEvaluacion(Long idProyecto) {
        ProyectoModel proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        List<EvaluacionDTO> evaluaciones = obtenerEvaluacionesPorProyecto(idProyecto);
        long totalCriterios = criterioRepository.count();

        ProyectoEvaluacionDTO dto = new ProyectoEvaluacionDTO();
        dto.setIdProyecto(proyecto.getIdProyecto());
        dto.setNombreProyecto(proyecto.getNombreProyecto());
        dto.setNombreEquipo(proyecto.getEquipo().getNombreEquipo());
        dto.setNombreHackaton(proyecto.getEquipo().getHackaton().getNombre());
        dto.setEvaluaciones(evaluaciones);
        dto.setEvaluacionesCompletadas(evaluaciones.size());
        dto.setEvaluacionesTotales((int) totalCriterios);
        dto.setEvaluacionCompleta(evaluaciones.size() >= totalCriterios);
        dto.setPuntajeFinal(calcularPuntajeFinalProyecto(idProyecto));

        return dto;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularPuntajeFinalProyecto(Long idProyecto) {
        List<EvaluacionModel> evaluaciones = evaluacionRepository.findByProyectoIdProyecto(idProyecto);

        if (evaluaciones.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal suma = evaluaciones.stream()
                .map(e -> e.getPuntuacion().multiply(e.getCriterio().getPeso()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return suma.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional(readOnly = true)
    public List<EvaluacionDTO> obtenerTodas() {
        return evaluacionRepository.findAll().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminar(Long id) {
        evaluacionRepository.deleteById(id);
    }

    private EvaluacionDTO convertirADTO(EvaluacionModel model) {
        EvaluacionDTO dto = new EvaluacionDTO();
        dto.setIdEvaluacion(model.getIdEvaluacion());
        dto.setIdProyecto(model.getProyecto().getIdProyecto());
        dto.setNombreProyecto(model.getProyecto().getNombreProyecto());
        dto.setNombreEquipo(model.getProyecto().getEquipo().getNombreEquipo());
        dto.setIdJurado(model.getJurado().getIdUsuario());
        dto.setNombreJurado(model.getJurado().getNombre() + " " + model.getJurado().getApellido());
        dto.setIdCriterio(model.getCriterio().getIdCriterio());
        dto.setNombreCriterio(model.getCriterio().getNombreCriterio());
        dto.setPeso(model.getCriterio().getPeso());
        dto.setPuntuacion(model.getPuntuacion());
        dto.setFeedback(model.getFeedback());
        dto.setFechaEvaluacion(model.getFechaEvaluacion());
        return dto;
    }
}
