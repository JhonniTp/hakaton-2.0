package com.hakaton.hakaton.dto;

import com.hakaton.hakaton.model.HackatonModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta con información completa de un Hackatón
 * Incluye datos adicionales como nombre de categoría, contadores, etc.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HackatonResponseDTO {

  private Long idHackaton;
  private String nombre;
  private String descripcion;
  private String urlImg;
  private LocalDateTime fechaInicio;
  private LocalDateTime fechaFin;
  private Integer maximoParticipantes;
  private Integer grupoCantidadParticipantes;
  private HackatonModel.Estado estado;
  private LocalDateTime fechaCreacion;

  // Información de la categoría
  private Long idCategoria;
  private String nombreCategoria;

  // Información adicional (se calculará en el servicio)
  private Integer totalInscritos;
  private Integer cuposDisponibles;
  private Boolean estaCompleto;

  /**
   * Constructor desde el modelo HackatonModel
   */
  public HackatonResponseDTO(HackatonModel hackaton) {
    this.idHackaton = hackaton.getIdHackaton();
    this.nombre = hackaton.getNombre();
    this.descripcion = hackaton.getDescripcion();
    this.urlImg = hackaton.getUrlImg();
    this.fechaInicio = hackaton.getFechaInicio();
    this.fechaFin = hackaton.getFechaFin();
    this.maximoParticipantes = hackaton.getMaximoParticipantes();
    this.grupoCantidadParticipantes = hackaton.getGrupoCantidadParticipantes();
    this.estado = hackaton.getEstado();
    this.fechaCreacion = hackaton.getFechaCreacion();

    if (hackaton.getCategoria() != null) {
      this.idCategoria = hackaton.getCategoria().getIdCategoria();
      this.nombreCategoria = hackaton.getCategoria().getNombreCategoria();
    }

    // Los valores de inscritos se establecerán desde el servicio
    this.totalInscritos = 0;
    this.cuposDisponibles = hackaton.getMaximoParticipantes();
    this.estaCompleto = false;
  }
}
