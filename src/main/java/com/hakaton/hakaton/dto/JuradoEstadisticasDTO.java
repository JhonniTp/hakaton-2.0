package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JuradoEstadisticasDTO {

    private Integer proyectosAsignados;
    private Integer proyectosEvaluados;
    private Integer proyectosPendientes;
    private Double promedioCalificacion;
    private Integer hackatonesActivos;
    private Integer evaluacionesMes;
}
