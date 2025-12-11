package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.EquipoDTO;
import com.hakaton.hakaton.dto.ParticipanteEquipoDTO;

import java.util.List;

public interface EquipoService {

    List<EquipoDTO> obtenerTodosLosEquipos();

    EquipoDTO obtenerEquipoPorId(Long idEquipo);
    List<EquipoDTO> obtenerEquiposPorHackaton(Long idHackaton);
    EquipoDTO crearEquipo(EquipoDTO equipoDTO);
    EquipoDTO actualizarEquipo(Long idEquipo, EquipoDTO equipoDTO);
    void eliminarEquipo(Long idEquipo);
    ParticipanteEquipoDTO agregarParticipante(Long idEquipo, Long idUsuario, boolean esLider);
    void removerParticipante(Long idParticipanteEquipo);
    List<ParticipanteEquipoDTO> obtenerParticipantesDeEquipo(Long idEquipo);
    void cambiarLider(Long idEquipo, Long idUsuario);
}
