package com.hakaton.hakaton.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para crear o actualizar un Hackatón
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HackatonDTO {

  @NotBlank(message = "El nombre del hackatón es obligatorio")
  @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
  private String nombre;

  @Size(max = 5000, message = "La descripción no puede exceder 5000 caracteres")
  private String descripcion;

  @Size(max = 255, message = "La URL de la imagen no puede exceder 255 caracteres")
  private String urlImg;

  @NotNull(message = "La fecha de inicio es obligatoria")
  @Future(message = "La fecha de inicio debe ser en el futuro")
  private LocalDateTime fechaInicio;

  @NotNull(message = "La fecha de fin es obligatoria")
  private LocalDateTime fechaFin;

  @NotNull(message = "El número máximo de participantes es obligatorio")
  @Min(value = 1, message = "Debe haber al menos 1 participante")
  @Max(value = 10000, message = "El máximo de participantes no puede exceder 10000")
  private Integer maximoParticipantes;

  @NotNull(message = "La cantidad de participantes por equipo es obligatoria")
  @Min(value = 1, message = "Los equipos deben tener al menos 1 participante")
  @Max(value = 20, message = "Los equipos no pueden tener más de 20 participantes")
  private Integer grupoCantidadParticipantes;

  @NotNull(message = "La categoría es obligatoria")
  private Long idCategoria;

  /**
   * Validación personalizada: la fecha de fin debe ser después de la fecha de
   * inicio
   */
  @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
  public boolean isFechaFinDespuesDeFechaInicio() {
    if (fechaInicio == null || fechaFin == null) {
      return true; // Dejar que @NotNull maneje nulos
    }
    return fechaFin.isAfter(fechaInicio);
  }
}
