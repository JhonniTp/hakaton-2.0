package com.hakaton.hakaton.model;

import java.time.LocalDateTime;
import com.hakaton.hakaton.model.enums.EstadoHackaton;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "hackatones")
public class Hackaton {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    private String urlImgPortada;

    @Lob
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Lob
    private String reglas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoHackaton estado;

    @Column(updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;
}