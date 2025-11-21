package com.hakaton.hakaton.dto;

import com.hakaton.hakaton.model.JuradoHackatonModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class JuradoHackatonDTO {

    private Long idJuradoHackaton;
    private Long idJurado;
    private String nombreJurado;
    private String apellidoJurado;
    private String correoJurado;
    private Long idHackaton;
    private String nombreHackaton;
    private LocalDateTime fechaAsignacion;

    
    public JuradoHackatonDTO(JuradoHackatonModel model) {
        this.idJuradoHackaton = model.getIdJuradoHackaton();
        this.idJurado = model.getJurado().getIdUsuario();
        this.nombreJurado = model.getJurado().getNombre();
        this.apellidoJurado = model.getJurado().getApellido();
        this.correoJurado = model.getJurado().getCorreoElectronico();
        this.idHackaton = model.getHackaton().getIdHackaton();
        this.nombreHackaton = model.getHackaton().getNombre();
        this.fechaAsignacion = model.getFechaAsignacion();
    }

    
    public JuradoHackatonDTO(Long idJuradoHackaton, Long idJurado, String nombreJurado,
            String apellidoJurado, String correoJurado, Long idHackaton,
            String nombreHackaton, LocalDateTime fechaAsignacion) {
        this.idJuradoHackaton = idJuradoHackaton;
        this.idJurado = idJurado;
        this.nombreJurado = nombreJurado;
        this.apellidoJurado = apellidoJurado;
        this.correoJurado = correoJurado;
        this.idHackaton = idHackaton;
        this.nombreHackaton = nombreHackaton;
        this.fechaAsignacion = fechaAsignacion;
    }

    public String getNombreCompletoJurado() {
        return nombreJurado + " " + apellidoJurado;
    }
}
