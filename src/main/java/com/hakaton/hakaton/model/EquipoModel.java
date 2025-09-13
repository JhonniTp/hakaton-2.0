package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Equipos")
@Getter
@Setter
public class EquipoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Long idEquipo;
    
    @Column(name = "img_url", nullable = false, length = 255)
    private String imgUrl;
    
    @Column(name = "nombre_equipo", nullable = false, length = 255)
    private String nombreEquipo;
    
    @ManyToOne
    @JoinColumn(name = "id_hackaton", nullable = false)
    private HackatonModel hackaton;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "equipo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ParticipanteEquipoModel> participantes;
    
    @OneToOne(mappedBy = "equipo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProyectoModel proyecto;
    
    // Constructor por defecto
    public EquipoModel() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Constructor con parámetros
    public EquipoModel(String imgUrl, String nombreEquipo, HackatonModel hackaton) {
        this.imgUrl = imgUrl;
        this.nombreEquipo = nombreEquipo;
        this.hackaton = hackaton;
        this.fechaCreacion = LocalDateTime.now();
    }
}
