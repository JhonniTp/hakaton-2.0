package com.hakaton.hakaton.service;

import com.hakaton.hakaton.dto.ActividadDTO;
import com.hakaton.hakaton.model.ActividadModel;
import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.ActividadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActividadService {

    private final ActividadRepository actividadRepository;

    public ActividadService(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    @Transactional
    public void registrarActividad(ActividadModel.TipoActividad tipo, String descripcion,
            String icono, String color, HackatonModel hackaton, UsuarioModel usuario) {
        ActividadModel actividad = new ActividadModel(tipo, descripcion, icono, color);
        actividad.setHackaton(hackaton);
        actividad.setUsuario(usuario);
        actividadRepository.save(actividad);
    }

    @Transactional(readOnly = true)
    public List<ActividadDTO> obtenerActividadesRecientes() {
        List<ActividadModel> actividades = actividadRepository.findTop20ByOrderByFechaActividadDesc();
        return actividades.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    private ActividadDTO convertirADTO(ActividadModel actividad) {
        ActividadDTO dto = new ActividadDTO();
        dto.setTipo(actividad.getTipo().name());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setIcono(actividad.getIcono());
        dto.setColor(actividad.getColor());
        dto.setTiempoTranscurrido(calcularTiempoTranscurrido(actividad.getFechaActividad()));
        return dto;
    }

    private String calcularTiempoTranscurrido(LocalDateTime fechaActividad) {
        Duration duracion = Duration.between(fechaActividad, LocalDateTime.now());

        long minutos = duracion.toMinutes();
        if (minutos < 1) {
            return "Hace unos segundos";
        } else if (minutos < 60) {
            return "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
        }

        long horas = duracion.toHours();
        if (horas < 24) {
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        }

        long dias = duracion.toDays();
        return "Hace " + dias + (dias == 1 ? " día" : " días");
    }
}
