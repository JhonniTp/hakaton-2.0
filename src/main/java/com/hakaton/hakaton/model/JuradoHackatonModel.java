package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "jurados_hackatones")
@EntityListeners(com.hakaton.hakaton.event.ActividadEventListener.class)
@Getter
@Setter
@NoArgsConstructor
public class JuradoHackatonModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jurado_hackaton")
    private Long idJuradoHackaton;

    @NotNull(message = "El jurado (usuario) es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_jurado", nullable = false)
    private UsuarioModel jurado;

    @NotNull(message = "El hackatón es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hackaton", nullable = false)
    private HackatonModel hackaton;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;

    @PrePersist
    protected void onCreate() {
        this.fechaAsignacion = LocalDateTime.now();
    }

    public JuradoHackatonModel(UsuarioModel jurado, HackatonModel hackaton) {
        this.jurado = jurado;
        this.hackaton = hackaton;
    }
}
