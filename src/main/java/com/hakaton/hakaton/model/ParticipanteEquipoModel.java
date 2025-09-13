package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Participantes_Equipos", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_usuario", "id_equipo"})
})
@Getter
@Setter
public class ParticipanteEquipoModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participante_equipo")
    private Long idParticipanteEquipo;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;
    
    @ManyToOne
    @JoinColumn(name = "id_equipo", nullable = false)
    private EquipoModel equipo;
    
    @Column(name = "es_lider")
    private Boolean esLider = false;
    
    // Constructor por defecto
    public ParticipanteEquipoModel() {
    }
    
    // Constructor con parámetros
    public ParticipanteEquipoModel(UsuarioModel usuario, EquipoModel equipo, Boolean esLider) {
        this.usuario = usuario;
        this.equipo = equipo;
        this.esLider = esLider;
    }
}