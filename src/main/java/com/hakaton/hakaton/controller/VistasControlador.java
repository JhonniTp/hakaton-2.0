package com.hakaton.hakaton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistasControlador { // La clase debe empezar después de todos los imports

    /**
     * Muestra la página de inicio de sesión y registro.
     */
    @GetMapping("/")
    public String vistaLogin() {
        return "login"; // Apunta a login.html
    }

    /**
     * Muestra el dashboard principal para el administrador.
     */
    @GetMapping("/admin/dashboard")
    public String vistaAdministrador() {
        return "admin/dashboard_admin";
    }

    /**
     * Muestra el dashboard principal para el jurado.
     */
    @GetMapping("/jurado/dashboard")
    public String vistaJurado() {
        return "jurado/dashboard_jurado";
    }

    /**
     * Muestra el dashboard principal para el participante.
     */
    @GetMapping("/participante/dashboard")
    public String vistaParticipante() {
        return "participante/dashboard_participante";
    }
}
