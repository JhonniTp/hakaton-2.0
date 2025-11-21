package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.dto.JuradoHackatonDTO;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.JuradoHackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.repository.JuradoHackatonRepository;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.JuradoHackatonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JuradoHackatonServiceImpl implements JuradoHackatonService {

    @Autowired
    private JuradoHackatonRepository juradoHackatonRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private HackatonRepository hackatonRepository;

    @Override
    public JuradoHackatonDTO asignarJurado(Long idJurado, Long idHackaton) {
        UsuarioModel jurado = usuarioRepository.findById(idJurado)
                .orElseThrow(() -> new RuntimeException("Jurado no encontrado con ID: " + idJurado));

        if (!jurado.getRol().equals(UsuarioModel.Rol.JURADO)) {
            throw new RuntimeException("El usuario no tiene rol de JURADO");
        }

        HackatonModel hackaton = hackatonRepository.findById(idHackaton)
                .orElseThrow(() -> new RuntimeException("Hackatón no encontrado con ID: " + idHackaton));

        if (juradoHackatonRepository.existsByJuradoIdAndHackatonId(idJurado, idHackaton)) {
            throw new RuntimeException("El jurado ya está asignado a este hackatón");
        }

        JuradoHackatonModel asignacion = new JuradoHackatonModel(jurado, hackaton);
        asignacion = juradoHackatonRepository.save(asignacion);

        return new JuradoHackatonDTO(asignacion);
    }

    @Override
    public void removerAsignacion(Long idJuradoHackaton) {
        if (!juradoHackatonRepository.existsById(idJuradoHackaton)) {
            throw new RuntimeException("Asignación no encontrada con ID: " + idJuradoHackaton);
        }
        juradoHackatonRepository.deleteById(idJuradoHackaton);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JuradoHackatonDTO> obtenerHackatonesDeJurado(Long idJurado) {
        if (!usuarioRepository.existsById(idJurado)) {
            throw new RuntimeException("Jurado no encontrado con ID: " + idJurado);
        }

        List<JuradoHackatonModel> asignaciones = juradoHackatonRepository.findHackatonesWithDetailsByJuradoId(idJurado);
        return asignaciones.stream()
                .map(JuradoHackatonDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JuradoHackatonDTO> obtenerJuradosDeHackaton(Long idHackaton) {
        if (!hackatonRepository.existsById(idHackaton)) {
            throw new RuntimeException("Hackatón no encontrado con ID: " + idHackaton);
        }

        List<JuradoHackatonModel> asignaciones = juradoHackatonRepository
                .findJuradosWithDetailsByHackatonId(idHackaton);
        return asignaciones.stream()
                .map(JuradoHackatonDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<JuradoHackatonDTO> obtenerTodasLasAsignaciones() {
        List<JuradoHackatonModel> asignaciones = juradoHackatonRepository.findAll();
        return asignaciones.stream()
                .map(JuradoHackatonDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaAsignado(Long idJurado, Long idHackaton) {
        return juradoHackatonRepository.existsByJuradoIdAndHackatonId(idJurado, idHackaton);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarJuradosPorHackaton(Long idHackaton) {
        return juradoHackatonRepository.countByHackatonId(idHackaton);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarHackatonesPorJurado(Long idJurado) {
        return juradoHackatonRepository.countByJuradoId(idJurado);
    }
}
