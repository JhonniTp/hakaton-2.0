package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HackathonActivoDTO {

    private Long idHackathon;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String estado;
    private String imgUrl;

    // Información del equipo del participante
    private Long idEquipo;
    private String nombreEquipo;
    private Integer miembrosEquipo;
    private Integer maxMiembros;

    // Información del proyecto
    private Long idProyecto;
    private String nombreProyecto;
    private Integer progresoProyecto; // Porcentaje 0-100
    private LocalDateTime fechaEntrega;

    // Días/horas restantes
    private Long horasRestantes;
    private Long diasRestantes;
}
