package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RankingParticipanteDTO {
    private int posicion;
    private Long idUsuario;
    private String nombreCompleto;
    private String carrera;
    private int hackathonesParticipados;
    private int proyectosCompletados;
    private double puntuacionPromedio;
    private int logrosObtenidos;
    private boolean esUsuarioActual; // Para destacar al usuario logueado
}
