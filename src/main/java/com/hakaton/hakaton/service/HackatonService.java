package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.HackatonDTO;
import com.hakaton.hakaton.model.HackatonModel;
import java.util.List;
import java.util.Optional;

public interface HackatonService {
    List<HackatonModel> obtenerTodosLosHackatones();
    Optional<HackatonModel> obtenerHackatonPorId(Long id);
    HackatonModel crearHackaton(HackatonDTO hackatonDTO);
    HackatonModel actualizarHackaton(Long id, HackatonDTO hackatonDTO);
    void eliminarHackaton(Long id);
    List<HackatonModel> obtenerHackatonesPorEstado(HackatonModel.Estado estado);
    List<HackatonModel> obtenerHackatonesPorCategoria(Long idCategoria);
    List<HackatonModel> buscarHackatonesPorNombre(String nombre);
    boolean existeNombre(String nombre);
}
