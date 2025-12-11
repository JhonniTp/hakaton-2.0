package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.dto.EquipoDTO;
import com.hakaton.hakaton.dto.ParticipanteEquipoDTO;
import com.hakaton.hakaton.model.EquipoModel;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.ParticipanteEquipoModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.EquipoRepository;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.repository.ParticipanteEquipoRepository;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EquipoServiceImpl implements EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private HackatonRepository hackatonRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipanteEquipoRepository participanteEquipoRepository;

    @Override
    public List<EquipoDTO> obtenerTodosLosEquipos() {
        return equipoRepository.findAll().stream()
                .map(this::convertirAEquipoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EquipoDTO obtenerEquipoPorId(Long idEquipo) {
        EquipoModel equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + idEquipo));
        return convertirAEquipoDTO(equipo);
    }

    @Override
    public List<EquipoDTO> obtenerEquiposPorHackaton(Long idHackaton) {
        return equipoRepository.findByHackatonId(idHackaton).stream()
                .map(this::convertirAEquipoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EquipoDTO crearEquipo(EquipoDTO equipoDTO) {
        HackatonModel hackaton = hackatonRepository.findById(equipoDTO.getIdHackaton())
                .orElseThrow(() -> new RuntimeException("Hackatón no encontrado con ID: " + equipoDTO.getIdHackaton()));

        if (equipoRepository.existsByNombreEquipoAndHackatonId(equipoDTO.getNombreEquipo(),
                equipoDTO.getIdHackaton())) {
            throw new RuntimeException(
                    "Ya existe un equipo con el nombre '" + equipoDTO.getNombreEquipo() + "' en este hackatón");
        }

        EquipoModel equipo = new EquipoModel();
        equipo.setNombreEquipo(equipoDTO.getNombreEquipo());
        equipo.setHackaton(hackaton);
        equipo.setImgUrl(equipoDTO.getImgUrl());

        EquipoModel equipoGuardado = equipoRepository.save(equipo);
        return convertirAEquipoDTO(equipoGuardado);
    }

    @Override
    public EquipoDTO actualizarEquipo(Long idEquipo, EquipoDTO equipoDTO) {
        EquipoModel equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + idEquipo));

        if (!equipo.getNombreEquipo().equals(equipoDTO.getNombreEquipo())) {
            if (equipoRepository.existsByNombreEquipoAndHackatonId(equipoDTO.getNombreEquipo(),
                    equipo.getHackaton().getIdHackaton())) {
                throw new RuntimeException(
                        "Ya existe un equipo con el nombre '" + equipoDTO.getNombreEquipo() + "' en este hackatón");
            }
        }

        equipo.setNombreEquipo(equipoDTO.getNombreEquipo());
        equipo.setImgUrl(equipoDTO.getImgUrl());

        EquipoModel equipoActualizado = equipoRepository.save(equipo);
        return convertirAEquipoDTO(equipoActualizado);
    }

    @Override
    public void eliminarEquipo(Long idEquipo) {
        if (!equipoRepository.existsById(idEquipo)) {
            throw new RuntimeException("Equipo no encontrado con ID: " + idEquipo);
        }
        equipoRepository.deleteById(idEquipo);
    }

    @Override
    public ParticipanteEquipoDTO agregarParticipante(Long idEquipo, Long idUsuario, boolean esLider) {
        EquipoModel equipo = equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + idEquipo));

        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + idUsuario));

        if (usuario.getRol() != UsuarioModel.Rol.PARTICIPANTE) {
            throw new RuntimeException("El usuario debe tener rol PARTICIPANTE");
        }

        if (participanteEquipoRepository.existsByUsuarioIdAndEquipoId(idUsuario, idEquipo)) {
            throw new RuntimeException("El usuario ya pertenece a este equipo");
        }

        if (participanteEquipoRepository.existsByUsuarioIdAndHackatonId(idUsuario,
                equipo.getHackaton().getIdHackaton())) {
            throw new RuntimeException("El usuario ya pertenece a otro equipo en este hackatón");
        }

        if (esLider) {
            participanteEquipoRepository.findLiderByEquipoId(idEquipo)
                    .ifPresent(liderActual -> {
                        liderActual.setEsLider(false);
                        participanteEquipoRepository.save(liderActual);
                    });
        }

        ParticipanteEquipoModel participanteEquipo = new ParticipanteEquipoModel();
        participanteEquipo.setUsuario(usuario);
        participanteEquipo.setEquipo(equipo);
        participanteEquipo.setEsLider(esLider);

        ParticipanteEquipoModel guardado = participanteEquipoRepository.save(participanteEquipo);
        return new ParticipanteEquipoDTO(guardado);
    }

    @Override
    public void removerParticipante(Long idParticipanteEquipo) {
        if (!participanteEquipoRepository.existsById(idParticipanteEquipo)) {
            throw new RuntimeException("Participante no encontrado con ID: " + idParticipanteEquipo);
        }
        participanteEquipoRepository.deleteById(idParticipanteEquipo);
    }

    @Override
    public List<ParticipanteEquipoDTO> obtenerParticipantesDeEquipo(Long idEquipo) {
        return participanteEquipoRepository.findByEquipoId(idEquipo).stream()
                .map(ParticipanteEquipoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public void cambiarLider(Long idEquipo, Long idUsuario) {
        equipoRepository.findById(idEquipo)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + idEquipo));

        List<ParticipanteEquipoModel> participantes = participanteEquipoRepository.findByEquipoId(idEquipo);
        ParticipanteEquipoModel nuevoLider = participantes.stream()
                .filter(p -> p.getUsuario().getIdUsuario().equals(idUsuario))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El usuario no pertenece a este equipo"));

        participanteEquipoRepository.findLiderByEquipoId(idEquipo)
                .ifPresent(liderActual -> {
                    liderActual.setEsLider(false);
                    participanteEquipoRepository.save(liderActual);
                });

        nuevoLider.setEsLider(true);
        participanteEquipoRepository.save(nuevoLider);
    }

    private EquipoDTO convertirAEquipoDTO(EquipoModel equipo) {
        EquipoDTO dto = new EquipoDTO(equipo);

        participanteEquipoRepository.findLiderByEquipoId(equipo.getIdEquipo())
                .ifPresent(lider -> {
                    UsuarioModel usuario = lider.getUsuario();
                    dto.setNombreLider(usuario.getNombre() + " " + usuario.getApellido());
                });

        return dto;
    }
}
