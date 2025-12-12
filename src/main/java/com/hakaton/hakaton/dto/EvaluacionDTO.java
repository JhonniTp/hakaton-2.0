package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionDTO {

    private Long idEvaluacion;
    private Long idProyecto;
    private String nombreProyecto;
    private String nombreEquipo;
    private Long idJurado;
    private String nombreJurado;
    private Long idCriterio;
    private String nombreCriterio;
    private BigDecimal peso;
    private BigDecimal puntuacion;
    private String feedback;
    private LocalDateTime fechaEvaluacion;
}
