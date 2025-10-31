package com.hakaton.hakaton.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hakaton.hakaton.model.UsuarioModel;

@Controller
@RequestMapping("/participante")
public class ParticipanteController {

    @GetMapping("/dashboard")
    public String mostrarDashboard(@AuthenticationPrincipal UsuarioModel usuario, Model model) {
        model.addAttribute("usuario", usuario);
        return "participante/dashboard_participantes"; // mantén el nombre tal como está tu plantilla
    }
}
