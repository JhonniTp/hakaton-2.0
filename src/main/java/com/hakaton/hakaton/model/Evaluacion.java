package com.hakaton.hakaton.model;

import java.time.LocalDateTime;


import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "Evaluaciones")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int puntuacionInnovacion;
    private int puntuacionTecnica;
    private int puntuacionPresentacion;
    private int puntuacionTotal;

    @Lob
    private String feedback;

    @Column(updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaEvaluacion;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @ManyToOne
    @JoinColumn(name = "jurado_id", nullable = false)
    private Usuario jurado;
}
