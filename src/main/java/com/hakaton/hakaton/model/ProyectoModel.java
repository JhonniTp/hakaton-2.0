package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa un proyecto entregado por un equipo en un hackatón.
 */
@Entity
@Table(name = "proyectos")
@Getter
@Setter
@NoArgsConstructor
public class ProyectoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Long idProyecto;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    @Size(max = 255, message = "El nombre del proyecto no puede exceder los 255 caracteres")
    @Column(name = "nombre_proyecto", nullable = false)
    private String nombreProyecto;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "El equipo es obligatorio")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false, unique = true)
    private EquipoModel equipo;

    @Size(max = 255, message = "La URL del entregable no puede exceder los 255 caracteres")
    @Column(name = "url_entregable")
    private String urlEntregable;

    @Size(max = 255, message = "La URL de la presentación no puede exceder los 255 caracteres")
    @Column(name = "url_presentacion")
    private String urlPresentacion;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluacionModel> evaluaciones = new ArrayList<>();


    public ProyectoModel(String nombreProyecto, EquipoModel equipo) {
        this.nombreProyecto = nombreProyecto;
        this.equipo = equipo;
        this.fechaEntrega = LocalDateTime.now();
    }
}
