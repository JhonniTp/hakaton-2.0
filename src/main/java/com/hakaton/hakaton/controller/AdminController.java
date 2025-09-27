package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/admin") 
public class AdminController {

    private final UsuarioService usuarioService;

    @Autowired
    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Maneja la solicitud GET para mostrar el dashboard del administrador.
     * Obtiene la lista de todos los usuarios y la pasa a la vista.
     * @param model El objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para renderizar.
     */
    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        List<UsuarioModel> listaUsuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", listaUsuarios);
        return "admin/dashboard_admin";
    }
}
