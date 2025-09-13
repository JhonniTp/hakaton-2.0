package com.hakaton.hakaton.service.imple;

import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.service.HackatonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HackatonServiceImpl implements HackatonService {

    @Autowired
    private HackatonRepository hackatonRepository;

    @Override
    public HackatonModel crearHackaton(HackatonModel hackaton) {
        // Validar fechas
        validarFechasHackaton(hackaton.getFechaInicio(), hackaton.getFechaFin());
        
        // Validar que el jurado asignado sea realmente un jurado
        if (hackaton.getJuradoAsignado() != null && 
            !hackaton.getJuradoAsignado().getRol().equals(UsuarioModel.Rol.jurado)) {
            throw new RuntimeException("El usuario asignado como jurado debe tener el rol 'jurado'");
        }
        
        // Validar capacidad de participantes
        if (hackaton.getMaximoParticipantes() <= 0) {
            throw new RuntimeException("El máximo de participantes debe ser mayor a 0");
        }
        
        if (hackaton.getGrupoCantidadParticipantes() <= 0) {
            throw new RuntimeException("La cantidad de participantes por grupo debe ser mayor a 0");
        }
        
        if (hackaton.getGrupoCantidadParticipantes() > hackaton.getMaximoParticipantes()) {
            throw new RuntimeException("La cantidad de participantes por grupo no puede ser mayor al máximo de participantes");
        }
        
        return hackatonRepository.save(hackaton);
    }

    @Override
    public List<HackatonModel> obtenerTodosLosHackatones() {
        return hackatonRepository.findAll();
    }

    @Override
    public Optional<HackatonModel> obtenerHackatonPorId(Long id) {
        return hackatonRepository.findById(id);
    }

    @Override
    public List<HackatonModel> obtenerHackatonesPorEstado(HackatonModel.Estado estado) {
        return hackatonRepository.findByEstado(estado);
    }

    @Override
    public List<HackatonModel> obtenerHackatonesPorCategoria(Long categoriaId) {
        return hackatonRepository.findByCategoriaId(categoriaId);
    }

    @Override
    public List<HackatonModel> buscarHackatonesPorNombre(String nombre) {
        return hackatonRepository.buscarPorNombre(nombre);
    }

    @Override
    public List<HackatonModel> obtenerHackatonesActivos() {
        return hackatonRepository.findHackatonesActivos(LocalDateTime.now());
    }

    @Override
    public List<HackatonModel> obtenerHackatonesPorJurado(Long juradoId) {
        return hackatonRepository.findByJuradoAsignadoId(juradoId);
    }

    @Override
    public List<HackatonModel> obtenerHackatonesConPlazasDisponibles() {
        return hackatonRepository.findHackatonesConPlazasDisponibles();
    }

    @Override
    public List<HackatonModel> obtenerHackatonesPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return hackatonRepository.findHackatonesPorRangoFechas(fechaInicio, fechaFin);
    }

    @Override
    public HackatonModel actualizarHackaton(Long id, HackatonModel hackatonActualizado) {
        return hackatonRepository.findById(id)
                .map(hackatonExistente -> {
                    // Validar fechas
                    validarFechasHackaton(
                        hackatonActualizado.getFechaInicio(), 
                        hackatonActualizado.getFechaFin()
                    );
                    
                    // Validar que el jurado asignado sea realmente un jurado
                    if (hackatonActualizado.getJuradoAsignado() != null && 
                        !hackatonActualizado.getJuradoAsignado().getRol().equals(UsuarioModel.Rol.jurado)) {
                        throw new RuntimeException("El usuario asignado como jurado debe tener el rol 'jurado'");
                    }
                    
                    // Validar capacidad de participantes
                    if (hackatonActualizado.getMaximoParticipantes() <= 0) {
                        throw new RuntimeException("El máximo de participantes debe ser mayor a 0");
                    }
                    
                    if (hackatonActualizado.getGrupoCantidadParticipantes() <= 0) {
                        throw new RuntimeException("La cantidad de participantes por grupo debe ser mayor a 0");
                    }
                    
                    if (hackatonActualizado.getGrupoCantidadParticipantes() > hackatonActualizado.getMaximoParticipantes()) {
                        throw new RuntimeException("La cantidad de participantes por grupo no puede ser mayor al máximo de participantes");
                    }
                    
                    // Actualizar campos
                    hackatonExistente.setUrlImg(hackatonActualizado.getUrlImg());
                    hackatonExistente.setNombre(hackatonActualizado.getNombre());
                    hackatonExistente.setDescripcion(hackatonActualizado.getDescripcion());
                    hackatonExistente.setCategoria(hackatonActualizado.getCategoria());
                    hackatonExistente.setFechaInicio(hackatonActualizado.getFechaInicio());
                    hackatonExistente.setFechaFin(hackatonActualizado.getFechaFin());
                    hackatonExistente.setMaximoParticipantes(hackatonActualizado.getMaximoParticipantes());
                    hackatonExistente.setGrupoCantidadParticipantes(hackatonActualizado.getGrupoCantidadParticipantes());
                    hackatonExistente.setEstado(hackatonActualizado.getEstado());
                    hackatonExistente.setJuradoAsignado(hackatonActualizado.getJuradoAsignado());
                    
                    return hackatonRepository.save(hackatonExistente);
                })
                .orElseThrow(() -> new RuntimeException("Hackaton no encontrado con ID: " + id));
    }

    @Override
    public void eliminarHackaton(Long id) {
        if (!hackatonRepository.existsById(id)) {
            throw new RuntimeException("Hackaton no encontrado con ID: " + id);
        }
        hackatonRepository.deleteById(id);
    }

    @Override
    public List<Object[]> obtenerEstadisticasPorEstado() {
        return hackatonRepository.contarPorEstado();
    }

    // Método auxiliar para validar fechas del hackaton
    private void validarFechasHackaton(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new RuntimeException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        
        if (fechaInicio.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("La fecha de inicio no puede ser en el pasado");
        }
    }
}