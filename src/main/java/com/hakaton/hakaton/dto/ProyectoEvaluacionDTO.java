package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoEvaluacionDTO {

    private Long idProyecto;
    private String nombreProyecto;
    private String nombreEquipo;
    private String nombreHackaton;
    private BigDecimal puntajeFinal;
    private Integer evaluacionesCompletadas;
    private Integer evaluacionesTotales;
    private Boolean evaluacionCompleta;
    private List<EvaluacionDTO> evaluaciones;
}
