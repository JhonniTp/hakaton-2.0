package com.hakaton.hakaton.model;

import java.time.LocalDateTime;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "proyectos")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombreProyecto;

    @Lob
    private String descripcion;

    private String urlImgProyecto;
    private String urlRepositorio;
    private String urlPresentacion;
    private String urlVideo;

    @Column(updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaSubida;

    @OneToOne
    @JoinColumn(name = "equipo_id", referencedColumnName = "id", unique = true)
    private Equipo equipo;
}
