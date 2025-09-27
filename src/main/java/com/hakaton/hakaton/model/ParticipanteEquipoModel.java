package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "participantes_equipos")
@Getter
@Setter
@NoArgsConstructor
public class ParticipanteEquipoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participante_equipo")
    private Long idParticipanteEquipo;

    @NotNull(message = "El usuario es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private UsuarioModel usuario;

    @NotNull(message = "El equipo es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_equipo", nullable = false)
    private EquipoModel equipo;

    @NotNull(message = "Debe especificarse si el participante es líder")
    @Column(name = "es_lider", nullable = false)
    private boolean esLider = false;

    public ParticipanteEquipoModel(UsuarioModel usuario, EquipoModel equipo, boolean esLider) {
        this.usuario = usuario;
        this.equipo = equipo;
        this.esLider = esLider;
    }
}
