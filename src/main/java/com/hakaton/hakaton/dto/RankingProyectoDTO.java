package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingProyectoDTO {

    private Integer posicion;
    private Long idProyecto;
    private String nombreProyecto;
    private String nombreEquipo;
    private Double puntajeFinal;
    private Integer totalEvaluaciones;
    private Integer evaluacionesCompletadas;
    private String estado;
}
