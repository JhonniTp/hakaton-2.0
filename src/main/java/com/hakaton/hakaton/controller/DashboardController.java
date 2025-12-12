package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.ActividadDTO;
import com.hakaton.hakaton.dto.EstadisticasDashboardDTO;
import com.hakaton.hakaton.service.ActividadService;
import com.hakaton.hakaton.service.DashboardService;
import com.hakaton.hakaton.service.ExportService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final ActividadService actividadService;
    private final ExportService exportService;

    public DashboardController(DashboardService dashboardService,
            ActividadService actividadService,
            ExportService exportService) {
        this.dashboardService = dashboardService;
        this.actividadService = actividadService;
        this.exportService = exportService;
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasDashboardDTO> obtenerEstadisticas() {
        EstadisticasDashboardDTO estadisticas = dashboardService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/actividades")
    public ResponseEntity<List<ActividadDTO>> obtenerActividades() {
        List<ActividadDTO> actividades = actividadService.obtenerActividadesRecientes();
        return ResponseEntity.ok(actividades);
    }

    @GetMapping("/exportar")
    public ResponseEntity<ByteArrayResource> exportarDatos() {
        byte[] csvData = exportService.generarCSV();
        String nombreArchivo = exportService.generarNombreArchivo();

        ByteArrayResource resource = new ByteArrayResource(csvData);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvData.length)
                .body(resource);
    }
}
