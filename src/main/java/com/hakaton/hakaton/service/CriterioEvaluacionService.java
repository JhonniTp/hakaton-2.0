package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.CriterioEvaluacionDTO;
import com.hakaton.hakaton.model.CriterioEvaluacionModel;
import com.hakaton.hakaton.repository.CriterioEvaluacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CriterioEvaluacionService {

    private final CriterioEvaluacionRepository criterioRepository;

    public CriterioEvaluacionService(CriterioEvaluacionRepository criterioRepository) {
        this.criterioRepository = criterioRepository;
    }

    @Transactional(readOnly = true)
    public List<CriterioEvaluacionDTO> obtenerTodos() {
        return criterioRepository.findAllByOrderByPesoDesc().stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CriterioEvaluacionDTO obtenerPorId(Long id) {
        CriterioEvaluacionModel criterio = criterioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));
        return convertirADTO(criterio);
    }

    @Transactional
    public CriterioEvaluacionDTO crear(CriterioEvaluacionDTO dto) {
        if (criterioRepository.existsByNombreCriterio(dto.getNombreCriterio())) {
            throw new RuntimeException("Ya existe un criterio con ese nombre");
        }

        CriterioEvaluacionModel criterio = new CriterioEvaluacionModel();
        criterio.setNombreCriterio(dto.getNombreCriterio());
        criterio.setDescripcion(dto.getDescripcion());
        criterio.setPeso(dto.getPeso());

        CriterioEvaluacionModel guardado = criterioRepository.save(criterio);
        return convertirADTO(guardado);
    }

    @Transactional
    public CriterioEvaluacionDTO actualizar(Long id, CriterioEvaluacionDTO dto) {
        CriterioEvaluacionModel criterio = criterioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Criterio no encontrado"));

        criterio.setNombreCriterio(dto.getNombreCriterio());
        criterio.setDescripcion(dto.getDescripcion());
        criterio.setPeso(dto.getPeso());

        CriterioEvaluacionModel actualizado = criterioRepository.save(criterio);
        return convertirADTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        long evaluacionesCount = criterioRepository.countEvaluacionesByCriterio(id);
        if (evaluacionesCount > 0) {
            throw new RuntimeException("No se puede eliminar el criterio porque tiene evaluaciones asociadas");
        }
        criterioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSumaPesos() {
        BigDecimal suma = criterioRepository.sumAllPesos();
        return suma != null ? suma : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public boolean validarSumaPesos() {
        BigDecimal suma = calcularSumaPesos();
        return suma.compareTo(BigDecimal.ONE) == 0;
    }

    private CriterioEvaluacionDTO convertirADTO(CriterioEvaluacionModel model) {
        CriterioEvaluacionDTO dto = new CriterioEvaluacionDTO();
        dto.setIdCriterio(model.getIdCriterio());
        dto.setNombreCriterio(model.getNombreCriterio());
        dto.setDescripcion(model.getDescripcion());
        dto.setPeso(model.getPeso());
        return dto;
    }
}
