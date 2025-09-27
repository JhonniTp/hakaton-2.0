package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "evaluaciones")
@Getter
@Setter
@NoArgsConstructor
public class EvaluacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Long idEvaluacion;

    @NotNull(message = "La puntuación es obligatoria")
    @DecimalMin(value = "0.0", message = "La puntuación no puede ser menor que 0")
    @DecimalMax(value = "5.0", message = "La puntuación no puede ser mayor que 5")
    @Column(name = "puntuacion", nullable = false, precision = 5, scale = 2)
    private BigDecimal puntuacion;

    @Lob
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @NotNull(message = "El jurado (usuario) es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jurado", nullable = false)
    private UsuarioModel jurado;

    @NotNull(message = "El criterio de evaluación es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_criterio", nullable = false)
    private CriterioEvaluacionModel criterio;

    @NotNull(message = "El proyecto es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", nullable = false)
    private ProyectoModel proyecto; // Nota: Se asume que existe un ProyectoModel

    @Column(name = "fecha_evaluacion", nullable = false, updatable = false)
    private LocalDateTime fechaEvaluacion;

    /**
     * Establece la fecha de evaluación automáticamente antes de persistir la entidad.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaEvaluacion = LocalDateTime.now();
    }

    /**
     * Constructor para crear una nueva evaluación.
     * @param puntuacion La puntuación asignada.
     * @param jurado El jurado que realiza la evaluación.
     * @param criterio El criterio que se está evaluando.
     * @param proyecto El proyecto evaluado.
     */
    public EvaluacionModel(BigDecimal puntuacion, UsuarioModel jurado, CriterioEvaluacionModel criterio, ProyectoModel proyecto) {
        this.puntuacion = puntuacion;
        this.jurado = jurado;
        this.criterio = criterio;
        this.proyecto = proyecto;
    }
}
