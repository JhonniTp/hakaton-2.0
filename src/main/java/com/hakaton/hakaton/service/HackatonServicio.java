package com.hakaton.hakaton.service;

import java.util.List;
import java.util.Optional;

import com.hakaton.hakaton.model.Hackaton;

public interface HackatonServicio {

    List<Hackaton> obtenerTodos();

    Optional<Hackaton> obtenerPorId(Integer id);

    Hackaton crearHackaton(Hackaton hackaton);

    Hackaton actualizarHackaton(Integer id, Hackaton detallesHackaton);

    void eliminarHackaton(Integer id);
}
