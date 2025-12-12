package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CriterioEvaluacionDTO {

    private Long idCriterio;
    private String nombreCriterio;
    private String descripcion;
    private BigDecimal peso;
    private String pesoFormatted;

    public String getPesoFormatted() {
        if (peso != null) {
            return String.format("%.0f%%", peso.multiply(new BigDecimal("100")));
        }
        return "0%";
    }
}
