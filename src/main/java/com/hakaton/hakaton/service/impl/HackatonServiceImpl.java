package com.hakaton.hakaton.service.impl;

import com.hakaton.hakaton.dto.HackatonDTO;
import com.hakaton.hakaton.model.CategoriaModel;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.repository.CategoriaRepository;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.service.HackatonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HackatonServiceImpl implements HackatonService {

    private final HackatonRepository hackatonRepository;
    private final CategoriaRepository categoriaRepository;

    public HackatonServiceImpl(HackatonRepository hackatonRepository,
            CategoriaRepository categoriaRepository) {
        this.hackatonRepository = hackatonRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HackatonModel> obtenerTodosLosHackatones() {
        return hackatonRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HackatonModel> obtenerHackatonPorId(Long id) {
        return hackatonRepository.findById(id);
    }

    @Override
    public HackatonModel crearHackaton(HackatonDTO hackatonDTO) {
        if (hackatonRepository.existsByNombre(hackatonDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un hackatón con ese nombre");
        }

        if (hackatonDTO.getFechaFin().isBefore(hackatonDTO.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        CategoriaModel categoria = categoriaRepository.findById(hackatonDTO.getIdCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        HackatonModel hackaton = new HackatonModel();
        hackaton.setNombre(hackatonDTO.getNombre());
        hackaton.setDescripcion(hackatonDTO.getDescripcion());
        hackaton.setUrlImg(hackatonDTO.getUrlImg());
        hackaton.setFechaInicio(hackatonDTO.getFechaInicio());
        hackaton.setFechaFin(hackatonDTO.getFechaFin());
        hackaton.setMaximoParticipantes(hackatonDTO.getMaximoParticipantes());
        hackaton.setGrupoCantidadParticipantes(hackatonDTO.getGrupoCantidadParticipantes());
        hackaton.setEstado(hackatonDTO.getEstado());
        hackaton.setCategoria(categoria);

        return hackatonRepository.save(hackaton);
    }

    @Override
    public HackatonModel actualizarHackaton(Long id, HackatonDTO hackatonDTO) {
        HackatonModel hackaton = hackatonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hackatón no encontrado"));

        if (!hackaton.getNombre().equals(hackatonDTO.getNombre()) &&
                hackatonRepository.existsByNombre(hackatonDTO.getNombre())) {
            throw new IllegalArgumentException("Ya existe un hackatón con ese nombre");
        }

        if (hackatonDTO.getFechaFin().isBefore(hackatonDTO.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        CategoriaModel categoria = categoriaRepository.findById(hackatonDTO.getIdCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        hackaton.setNombre(hackatonDTO.getNombre());
        hackaton.setDescripcion(hackatonDTO.getDescripcion());
        hackaton.setUrlImg(hackatonDTO.getUrlImg());
        hackaton.setFechaInicio(hackatonDTO.getFechaInicio());
        hackaton.setFechaFin(hackatonDTO.getFechaFin());
        hackaton.setMaximoParticipantes(hackatonDTO.getMaximoParticipantes());
        hackaton.setGrupoCantidadParticipantes(hackatonDTO.getGrupoCantidadParticipantes());
        hackaton.setEstado(hackatonDTO.getEstado());
        hackaton.setCategoria(categoria);

        return hackatonRepository.save(hackaton);
    }

    @Override
    public void eliminarHackaton(Long id) {
        if (!hackatonRepository.existsById(id)) {
            throw new IllegalArgumentException("Hackatón no encontrado");
        }
        hackatonRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HackatonModel> obtenerHackatonesPorEstado(HackatonModel.Estado estado) {
        return hackatonRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HackatonModel> obtenerHackatonesPorCategoria(Long idCategoria) {
        return hackatonRepository.findByCategoriaIdCategoria(idCategoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HackatonModel> buscarHackatonesPorNombre(String nombre) {
        return hackatonRepository.findByNombreContaining(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNombre(String nombre) {
        return hackatonRepository.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HackatonModel> obtenerHackatonesRecientes() {
        return hackatonRepository.findTop2ByOrderByFechaCreacionDesc();
    }
}
