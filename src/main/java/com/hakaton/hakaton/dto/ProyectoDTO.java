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
public class ProyectoDTO {
    private Long idProyecto;
    private String nombreProyecto;
    private String descripcion;
    private Long idEquipo;
    private String nombreEquipo;
    private String nombreHackathon;
    private String urlEntregable;
    private String urlPresentacion;
    private LocalDateTime fechaEntrega;
    private Double puntuacionPromedio;
    private String estado; // EN_PROGRESO, ENTREGADO, EVALUADO
}
