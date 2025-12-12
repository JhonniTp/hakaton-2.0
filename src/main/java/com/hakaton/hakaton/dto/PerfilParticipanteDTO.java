package com.hakaton.hakaton.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfilParticipanteDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private String telefono;
    private String carrera;
    private String ciclo;
    private String iniciales;
    private String rol;

    // Estadísticas
    private int totalHackathons;
    private int proyectosCompletados;
    private double puntuacionPromedio;
    private int logrosObtenidos;
    private int posicionRanking;

    // Habilidades y bio
    private String biografia;
    private List<String> habilidades;
    private String githubUrl;
    private String linkedinUrl;
}
