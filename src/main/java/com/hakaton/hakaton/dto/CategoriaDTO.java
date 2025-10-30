package com.hakaton.hakaton.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Categoría
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {

  private Long idCategoria;

  @NotBlank(message = "El nombre de la categoría es obligatorio")
  @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
  private String nombreCategoria;
}
