package com.hakaton.hakaton.event;

import com.hakaton.hakaton.model.*;
import com.hakaton.hakaton.service.ActividadService;
import jakarta.persistence.PostPersist;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class ActividadEventListener {

    private static ActividadService actividadService;

    public ActividadEventListener(@Lazy ActividadService actividadService) {
        ActividadEventListener.actividadService = actividadService;
    }

    @PostPersist
    public void onEntityCreated(Object entity) {
        if (entity instanceof InscripcionModel) {
            InscripcionModel inscripcion = (InscripcionModel) entity;
            actividadService.registrarActividad(
                    ActividadModel.TipoActividad.PARTICIPANTE_REGISTRADO,
                    inscripcion.getUsuario().getNombre() + " " + inscripcion.getUsuario().getApellido() +
                            " se unió a " + inscripcion.getHackaton().getNombre(),
                    "fa-user-plus",
                    "green-600",
                    inscripcion.getHackaton(),
                    inscripcion.getUsuario());
        } else if (entity instanceof ProyectoModel) {
            ProyectoModel proyecto = (ProyectoModel) entity;
            actividadService.registrarActividad(
                    ActividadModel.TipoActividad.PROYECTO_ENTREGADO,
                    "Equipo \"" + proyecto.getEquipo().getNombreEquipo() + "\" subió su proyecto",
                    "fa-upload",
                    "blue-600",
                    proyecto.getEquipo().getHackaton(),
                    null);
        } else if (entity instanceof EvaluacionModel) {
            EvaluacionModel evaluacion = (EvaluacionModel) entity;
            actividadService.registrarActividad(
                    ActividadModel.TipoActividad.EVALUACION_COMPLETADA,
                    evaluacion.getJurado().getNombre() + " evaluó \"" +
                            evaluacion.getProyecto().getNombreProyecto() + "\"",
                    "fa-star",
                    "yellow-600",
                    evaluacion.getProyecto().getEquipo().getHackaton(),
                    evaluacion.getJurado());
        } else if (entity instanceof EquipoModel) {
            EquipoModel equipo = (EquipoModel) entity;
            actividadService.registrarActividad(
                    ActividadModel.TipoActividad.EQUIPO_FORMADO,
                    "Nuevo equipo \"" + equipo.getNombreEquipo() + "\" creado",
                    "fa-users",
                    "purple-600",
                    equipo.getHackaton(),
                    null);
        } else if (entity instanceof HackatonModel) {
            HackatonModel hackaton = (HackatonModel) entity;
            actividadService.registrarActividad(
                    ActividadModel.TipoActividad.HACKATON_CREADO,
                    "Nuevo hackatón \"" + hackaton.getNombre() + "\" creado",
                    "fa-trophy",
                    "cyan-600",
                    hackaton,
                    null);
        } else if (entity instanceof JuradoHackatonModel) {
            JuradoHackatonModel juradoHackaton = (JuradoHackatonModel) entity;
            actividadService.registrarActividad(
                    ActividadModel.TipoActividad.JURADO_ASIGNADO,
                    juradoHackaton.getJurado().getNombre() + " " + juradoHackaton.getJurado().getApellido() +
                            " asignado como jurado",
                    "fa-gavel",
                    "pink-600",
                    juradoHackaton.getHackaton(),
                    juradoHackaton.getJurado());
        }
    }
}
