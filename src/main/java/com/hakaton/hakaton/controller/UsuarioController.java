package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        List<UsuarioModel> listaUsuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", listaUsuarios);
        return "admin/dashboard_admin";
    }
}