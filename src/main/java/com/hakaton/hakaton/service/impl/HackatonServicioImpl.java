package com.hakaton.hakaton.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hakaton.hakaton.model.Hackaton;
import com.hakaton.hakaton.repository.HackatonRepositorio;
import com.hakaton.hakaton.service.HackatonServicio;

@Service
public class HackatonServicioImpl implements HackatonServicio {

    @Autowired
    private HackatonRepositorio hackatonRepositorio;

    @Override
    public List<Hackaton> obtenerTodos() {
        return hackatonRepositorio.findAll();
    }

    @Override
    public Optional<Hackaton> obtenerPorId(Integer id) {
        return hackatonRepositorio.findById(id);
    }

    @Override
    public Hackaton crearHackaton(Hackaton hackaton) {
        // Aquí se podrían añadir validaciones antes de guardar
        return hackatonRepositorio.save(hackaton);
    }

    @Override
    public Hackaton actualizarHackaton(Integer id, Hackaton detallesHackaton) {
        Hackaton hackatonExistente = hackatonRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Hackaton no encontrado con el ID: " + id));

        hackatonExistente.setNombre(detallesHackaton.getNombre());
        hackatonExistente.setDescripcion(detallesHackaton.getDescripcion());
        hackatonExistente.setFechaInicio(detallesHackaton.getFechaInicio());
        hackatonExistente.setFechaFin(detallesHackaton.getFechaFin());
        hackatonExistente.setReglas(detallesHackaton.getReglas());
        hackatonExistente.setEstado(detallesHackaton.getEstado());
        hackatonExistente.setUrlImgPortada(detallesHackaton.getUrlImgPortada());

        return hackatonRepositorio.save(hackatonExistente);
    }

    @Override
    public void eliminarHackaton(Integer id) {
        // Se podría añadir lógica para verificar si el hackaton se puede eliminar
        hackatonRepositorio.deleteById(id);
    }
}
