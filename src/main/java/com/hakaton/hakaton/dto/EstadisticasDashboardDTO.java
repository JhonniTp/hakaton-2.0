package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasDashboardDTO {

    private Long hackatonesActivos;
    private Long incrementoHackatonesEsteMes;
    private Long totalParticipantes;
    private Long incrementoParticipantesEstaSemana;
    private Long proyectosEntregados;
    private Long incrementoProyectosHoy;
    private Long evaluacionesPendientes;
}
