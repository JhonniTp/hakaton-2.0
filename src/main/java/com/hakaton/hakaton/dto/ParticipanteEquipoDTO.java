package com.hakaton.hakaton.dto;

import com.hakaton.hakaton.model.ParticipanteEquipoModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteEquipoDTO {
    private Long idParticipanteEquipo;
    private Long idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private String correoUsuario;
    private Long idEquipo;
    private String nombreEquipo;
    private boolean esLider;


    public ParticipanteEquipoDTO(ParticipanteEquipoModel participanteEquipo) {
        this.idParticipanteEquipo = participanteEquipo.getIdParticipanteEquipo();
        this.idUsuario = participanteEquipo.getUsuario() != null ? participanteEquipo.getUsuario().getIdUsuario()
                : null;
        this.nombreUsuario = participanteEquipo.getUsuario() != null ? participanteEquipo.getUsuario().getNombre()
                : null;
        this.apellidoUsuario = participanteEquipo.getUsuario() != null ? participanteEquipo.getUsuario().getApellido()
                : null;
        this.correoUsuario = participanteEquipo.getUsuario() != null
                ? participanteEquipo.getUsuario().getCorreoElectronico()
                : null;
        this.idEquipo = participanteEquipo.getEquipo() != null ? participanteEquipo.getEquipo().getIdEquipo() : null;
        this.nombreEquipo = participanteEquipo.getEquipo() != null ? participanteEquipo.getEquipo().getNombreEquipo()
                : null;
        this.esLider = participanteEquipo.isEsLider();
    }

    public String getNombreCompleto() {
        return nombreUsuario + " " + apellidoUsuario;
    }
}
