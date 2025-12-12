package com.hakaton.hakaton.service;

import com.hakaton.hakaton.model.HackatonModel;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.EquipoRepository;
import com.hakaton.hakaton.repository.HackatonRepository;
import com.hakaton.hakaton.repository.ProyectoRepository;
import com.hakaton.hakaton.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private final HackatonRepository hackatonRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipoRepository equipoRepository;
    private final ProyectoRepository proyectoRepository;

    public ExportService(HackatonRepository hackatonRepository,
            UsuarioRepository usuarioRepository,
            EquipoRepository equipoRepository,
            ProyectoRepository proyectoRepository) {
        this.hackatonRepository = hackatonRepository;
        this.usuarioRepository = usuarioRepository;
        this.equipoRepository = equipoRepository;
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generarCSV() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        writer.println("REPORTE GENERAL DEL SISTEMA - " + LocalDateTime.now().format(formatter));
        writer.println();

        writer.println("ESTADÍSTICAS GENERALES");
        writer.println("Total de Hackatones," + hackatonRepository.count());
        writer.println("Total de Participantes," + usuarioRepository.countByRol(UsuarioModel.Rol.PARTICIPANTE));
        writer.println("Total de Equipos," + equipoRepository.count());
        writer.println("Total de Proyectos," + proyectoRepository.count());
        writer.println();

        writer.println("HACKATONES");
        writer.println("ID,Nombre,Fecha Inicio,Fecha Fin,Estado,Max Participantes");
        List<HackatonModel> hackatones = hackatonRepository.findAll();
        for (HackatonModel h : hackatones) {
            writer.printf("%d,%s,%s,%s,%s,%d%n",
                    h.getIdHackaton(),
                    escaparCSV(h.getNombre()),
                    h.getFechaInicio().format(formatter),
                    h.getFechaFin().format(formatter),
                    h.getEstado(),
                    h.getMaximoParticipantes());
        }
        writer.println();

        writer.println("PARTICIPANTES");
        writer.println("ID,Nombre,Apellido,Correo,Teléfono,Rol");
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        for (UsuarioModel u : usuarios) {
            writer.printf("%d,%s,%s,%s,%s,%s%n",
                    u.getIdUsuario(),
                    escaparCSV(u.getNombre()),
                    escaparCSV(u.getApellido()),
                    escaparCSV(u.getCorreoElectronico()),
                    escaparCSV(u.getTelefono()),
                    u.getRol());
        }

        writer.flush();
        return outputStream.toByteArray();
    }

    private String escaparCSV(String valor) {
        if (valor == null) {
            return "";
        }
        if (valor.contains(",") || valor.contains("\"") || valor.contains("\n")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    public String generarNombreArchivo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        return "dashboard_export_" + LocalDateTime.now().format(formatter) + ".csv";
    }
}
