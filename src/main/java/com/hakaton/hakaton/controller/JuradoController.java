package com.hakaton.hakaton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/jurado")
public class JuradoController {

    @GetMapping("/dashboard")
    public String mostrarDashboard() {
        return "jurado/dashboard_jurado";
    }
}
