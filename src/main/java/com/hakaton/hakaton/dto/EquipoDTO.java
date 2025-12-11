package com.hakaton.hakaton.dto;

import com.hakaton.hakaton.model.EquipoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDTO {
    private Long idEquipo;
    private String nombreEquipo;
    private Long idHackaton;
    private String nombreHackaton;
    private String imgUrl;
    private LocalDateTime fechaCreacion;
    private Integer cantidadMiembros;
    private String nombreLider;

    public EquipoDTO(EquipoModel equipo) {
        this.idEquipo = equipo.getIdEquipo();
        this.nombreEquipo = equipo.getNombreEquipo();
        this.idHackaton = equipo.getHackaton() != null ? equipo.getHackaton().getIdHackaton() : null;
        this.nombreHackaton = equipo.getHackaton() != null ? equipo.getHackaton().getNombre() : "Sin hackatón";
        this.imgUrl = equipo.getImgUrl();
        this.fechaCreacion = equipo.getFechaCreacion();
        this.cantidadMiembros = equipo.getParticipantes() != null ? equipo.getParticipantes().size() : 0;
    }
}
