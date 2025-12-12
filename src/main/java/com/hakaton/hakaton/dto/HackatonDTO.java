package com.hakaton.hakaton.dto;

import com.hakaton.hakaton.model.HackatonModel;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HackatonDTO {

    private Long idHackaton;

    @NotBlank(message = "El nombre del hackatón es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    private String nombre;

    private String descripcion;

    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    private String urlImg;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDateTime fechaFin;

    @NotNull(message = "El número máximo de participantes es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 participante como máximo")
    private Integer maximoParticipantes;

    @NotNull(message = "La cantidad de participantes por grupo es obligatoria")
    @Min(value = 1, message = "Los grupos deben tener al menos 1 participante")
    private Integer grupoCantidadParticipantes;

    @NotNull(message = "El estado es obligatorio")
    private HackatonModel.Estado estado;

    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;

    private String nombreCategoria;

    private LocalDateTime fechaCreacion;

    private boolean estaInscrito;
    private int participantesActuales;
    private int lugaresDisponibles;

    public HackatonDTO(HackatonModel hackaton) {
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
    }
}
