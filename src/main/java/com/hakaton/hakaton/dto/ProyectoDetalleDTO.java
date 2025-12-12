package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoDetalleDTO {

    private Long idProyecto;
    private String nombreProyecto;
    private String descripcion;
    private String urlEntregable;
    private String urlRepositorio;
    private String nombreEquipo;
    private Long idHackaton;
    private String nombreHackaton;
    private List<String> miembros;
    private Integer evaluacionesCompletadas;
    private Integer evaluacionesTotales;
    private String estado;
    private Double puntajePromedio;
    private Boolean yaEvalueEsteProyecto;
}
