package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Proyectos")
@Getter
@Setter
public class ProyectoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Long idProyecto;
    
    @OneToOne
    @JoinColumn(name = "id_equipo", nullable = false, unique = true)
    private EquipoModel equipo;
    
    @Column(name = "nombre_proyecto", nullable = false, length = 255)
    private String nombreProyecto;
    
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "url_entregable", length = 255)
    private String urlEntregable;
    
    @Column(name = "url_presentacion", length = 255)
    private String urlPresentacion;
    
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
    
    // Constructor por defecto
    public ProyectoModel() {
        this.fechaEntrega = LocalDateTime.now();
    }
    
    // Constructor con parámetros
    public ProyectoModel(EquipoModel equipo, String nombreProyecto, String descripcion, 
                        String urlEntregable, String urlPresentacion) {
        this.equipo = equipo;
        this.nombreProyecto = nombreProyecto;
        this.descripcion = descripcion;
        this.urlEntregable = urlEntregable;
        this.urlPresentacion = urlPresentacion;
        this.fechaEntrega = LocalDateTime.now();
    }
}