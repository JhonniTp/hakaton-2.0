package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoCronogramaDTO {

    private String tipo;
    private String hackaton;
    private LocalDateTime fecha;
    private String descripcion;
    private String prioridad;
}
