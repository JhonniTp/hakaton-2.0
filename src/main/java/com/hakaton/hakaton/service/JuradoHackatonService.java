package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.JuradoHackatonDTO;

import java.util.List;

public interface JuradoHackatonService {
    JuradoHackatonDTO asignarJurado(Long idJurado, Long idHackaton);
    void removerAsignacion(Long idJuradoHackaton);
    List<JuradoHackatonDTO> obtenerHackatonesDeJurado(Long idJurado);
    List<JuradoHackatonDTO> obtenerJuradosDeHackaton(Long idHackaton);
    List<JuradoHackatonDTO> obtenerTodasLasAsignaciones();
    boolean estaAsignado(Long idJurado, Long idHackaton);
    long contarJuradosPorHackaton(Long idHackaton);
    long contarHackatonesPorJurado(Long idJurado);
}
