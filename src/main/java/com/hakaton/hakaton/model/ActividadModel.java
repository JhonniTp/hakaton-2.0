package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "actividades")
@Getter
@Setter
@NoArgsConstructor
public class ActividadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Long idActividad;

    @NotNull(message = "El tipo de actividad es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoActividad tipo;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder los 500 caracteres")
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hackaton")
    private HackatonModel hackaton;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private UsuarioModel usuario;

    @Size(max = 50, message = "El icono no puede exceder los 50 caracteres")
    @Column(name = "icono", length = 50)
    private String icono;

    @Size(max = 50, message = "El color no puede exceder los 50 caracteres")
    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "fecha_actividad", nullable = false, updatable = false)
    private LocalDateTime fechaActividad;

    public enum TipoActividad {
        PARTICIPANTE_REGISTRADO,
        PROYECTO_ENTREGADO,
        EVALUACION_COMPLETADA,
        EQUIPO_FORMADO,
        HACKATON_CREADO,
        JURADO_ASIGNADO
    }

    @PrePersist
    protected void onCreate() {
        this.fechaActividad = LocalDateTime.now();
    }

    public ActividadModel(TipoActividad tipo, String descripcion, String icono, String color) {
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.icono = icono;
        this.color = color;
    }
}
