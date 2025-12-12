package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "hackatones")
@EntityListeners(com.hakaton.hakaton.event.ActividadEventListener.class)
@Getter
@Setter
@NoArgsConstructor
public class HackatonModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hackaton")
    private Long idHackaton;

    @NotBlank(message = "El nombre del hackatón es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder los 255 caracteres")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    @Column(name = "url_img")
    private String urlImg;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @FutureOrPresent(message = "La fecha de inicio debe ser en el presente o futuro")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @FutureOrPresent(message = "La fecha de fin debe ser en el presente o futuro")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @NotNull(message = "El número máximo de participantes es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 participante como máximo")
    @Column(name = "maximo_participantes", nullable = false)
    private Integer maximoParticipantes;

    @NotNull(message = "La cantidad de participantes por grupo es obligatoria")
    @Min(value = 1, message = "Los grupos deben tener al menos 1 participante")
    @Column(name = "grupo_cantidad_participantes", nullable = false)
    private Integer grupoCantidadParticipantes;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.PROXIMO;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaModel categoria;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public enum Estado {
        PROXIMO,
        EN_CURSO,
        FINALIZADO
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

}
