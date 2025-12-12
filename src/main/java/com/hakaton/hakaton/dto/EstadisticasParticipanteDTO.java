package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasParticipanteDTO {

    private Integer totalHackathons;
    private Integer hackatonesActivos;
    private Integer proyectosActivos;
    private Integer proyectosCompletados;
    private Double puntuacionPromedio;
    private Integer posicionRanking;
    private Integer totalParticipantes; 
    private Integer logrosObtenidos;
    private Integer equiposActivos;
}
