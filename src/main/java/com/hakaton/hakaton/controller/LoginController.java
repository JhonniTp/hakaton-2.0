package com.hakaton.hakaton.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping({"/", "/login"})
    public String mostrarLogin() {
        // devuelve el nombre del archivo login.html (sin la extensión)
        return "login";
    }
}
