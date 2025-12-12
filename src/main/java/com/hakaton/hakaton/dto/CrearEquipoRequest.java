package com.hakaton.hakaton.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearEquipoRequest {

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombreEquipo;

    @NotNull(message = "El ID del hackathon es obligatorio")
    private Long idHackaton;

    // IDs de los usuarios a agregar al equipo (opcional, se pueden agregar después)
    private List<Long> idsParticipantes;
}
