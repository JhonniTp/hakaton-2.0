package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado para mostrar información de usuario (jurado o participante)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSimpleDTO {
  private Long idUsuario;
  private String nombreCompleto;
  private String correoElectronico;
  private String rol;
  private String telefono;
}
