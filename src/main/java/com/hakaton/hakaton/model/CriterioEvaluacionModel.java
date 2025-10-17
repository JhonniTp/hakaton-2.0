package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;


@Entity
@Table(name = "criterios_evaluacion")
@Getter
@Setter
@NoArgsConstructor
public class CriterioEvaluacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_criterio")
    private Long idCriterio;

    @NotBlank(message = "El nombre del criterio es obligatorio")
    @Size(max = 255, message = "El nombre del criterio no puede exceder los 255 caracteres")
    @Column(name = "nombre_criterio", nullable = false, length = 255)
    private String nombreCriterio;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.01", message = "El peso debe ser mayor que 0")
    @DecimalMax(value = "1.00", message = "El peso no puede ser mayor que 1.00")
    @Column(name = "peso", nullable = false, precision = 3, scale = 2)
    private BigDecimal peso;


    public CriterioEvaluacionModel(String nombreCriterio, String descripcion, BigDecimal peso) {
        this.nombreCriterio = nombreCriterio;
        this.descripcion = descripcion;
        this.peso = peso;
    }
}
