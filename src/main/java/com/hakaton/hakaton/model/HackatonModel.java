package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Hackatones")
@Getter
@Setter
public class HackatonModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hackaton")
    private Long idHackaton;

    @Column(name = "url_img", nullable = false, length = 255)
    private String urlImg;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaModel categoria;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "maximo_participantes", nullable = false)
    private Integer maximoParticipantes;

    @Column(name = "grupo_cantidad_participantes", nullable = false)
    private Integer grupoCantidadParticipantes;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, columnDefinition = "ENUM('proximo', 'en_curso', 'finalizado')")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "id_jurado_asignado")
    private UsuarioModel juradoAsignado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Relaciones con otros modelos
    @OneToMany(mappedBy = "hackaton", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EquipoModel> equipos;

    @OneToMany(mappedBy = "hackaton", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InscripcionModel> inscripciones;

    // Enumeración para el estado
    public enum Estado {
        proximo, en_curso, finalizado
    }

    // Constructor por defecto
    public HackatonModel() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = Estado.proximo;
    }

    // Constructor con parámetros básicos
    public HackatonModel(String urlImg, String nombre, String descripcion, CategoriaModel categoria,
            LocalDateTime fechaInicio, LocalDateTime fechaFin, Integer maximoParticipantes,
            Integer grupoCantidadParticipantes, Estado estado, UsuarioModel juradoAsignado) {
        this.urlImg = urlImg;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.maximoParticipantes = maximoParticipantes;
        this.grupoCantidadParticipantes = grupoCantidadParticipantes;
        this.estado = estado;
        this.juradoAsignado = juradoAsignado;
        this.fechaCreacion = LocalDateTime.now();
    }
}