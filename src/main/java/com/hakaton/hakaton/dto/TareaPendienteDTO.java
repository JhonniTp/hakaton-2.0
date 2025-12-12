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
public class TareaPendienteDTO {

    private String titulo;
    private String descripcion;
    private String tipo; // ENTREGA, PRESENTACION, REUNION, EVALUACION
    private LocalDateTime fechaLimite;
    private String prioridad; // ALTA, MEDIA, BAJA
    private String hackathon;
    private Long idRelacionado; // ID del proyecto/equipo relacionado
    private Integer diasRestantes;
}
