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
 * Representa un equipo que participa en un hackatón.
 */
@Entity
@Table(name = "equipos")
@Getter
@Setter
@NoArgsConstructor
public class EquipoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Long idEquipo;

    @NotBlank(message = "El nombre del equipo es obligatorio")
    @Size(max = 255, message = "El nombre del equipo no puede exceder los 255 caracteres")
    @Column(name = "nombre_equipo", nullable = false)
    private String nombreEquipo;

    @NotNull(message = "El hackatón es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hackaton", nullable = false)
    private HackatonModel hackaton;

    @Size(max = 255, message = "La URL de la imagen no puede exceder los 255 caracteres")
    @Column(name = "img_url")
    private String imgUrl;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipanteEquipoModel> participantes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    /**
     * Constructor para crear un nuevo equipo.
     * @param nombreEquipo El nombre del equipo.
     * @param hackaton El hackatón en el que participa el equipo.
     */
    public EquipoModel(String nombreEquipo, HackatonModel hackaton) {
        this.nombreEquipo = nombreEquipo;
        this.hackaton = hackaton;
    }
}
