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
public class LogroDTO {

    private String titulo;
    private String descripcion;
    private String icono; // emoji o clase de Font Awesome
    private String tipo; // PREMIO, POSICION, HABILIDAD, RACHA
    private LocalDateTime fechaObtencion;
    private String hackathon; // Puede ser null si es logro general
    private String color; // Para el badge: yellow, blue, green, etc.
}
