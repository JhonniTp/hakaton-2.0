package com.hakaton.hakaton.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarProyectoRequest {

    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String nombreProyecto;

    private String descripcion;

    @Size(max = 255, message = "La URL del entregable no puede exceder 255 caracteres")
    private String urlEntregable;

    @Size(max = 255, message = "La URL de la presentación no puede exceder 255 caracteres")
    private String urlPresentacion;
}
