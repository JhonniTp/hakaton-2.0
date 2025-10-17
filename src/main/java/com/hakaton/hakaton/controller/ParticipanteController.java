package com.hakaton.hakaton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/participante")
public class ParticipanteController {

    @GetMapping("/dashboard")
    public String mostrarDashboard() {
        return "participante/dashboard_participantes";
    }
}
