package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Inscripciones", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_usuario", "id_hackaton"})
})
@Getter
@Setter
public class InscripcionModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscripcion")
    private Long idInscripcion;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_hackaton", nullable = false)
    private HackatonModel hackaton;
    
    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;
    
    // Constructor por defecto
    public InscripcionModel() {
        this.fechaInscripcion = LocalDateTime.now();
    }
    
    // Constructor con parámetros
    public InscripcionModel(UsuarioModel usuario, HackatonModel hackaton) {
        this.usuario = usuario;
        this.hackaton = hackaton;
        this.fechaInscripcion = LocalDateTime.now();
    }
}