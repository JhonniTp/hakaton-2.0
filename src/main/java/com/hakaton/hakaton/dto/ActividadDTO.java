package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActividadDTO {

    private String tipo;
    private String descripcion;
    private String icono;
    private String color;
    private String tiempoTranscurrido;
}
