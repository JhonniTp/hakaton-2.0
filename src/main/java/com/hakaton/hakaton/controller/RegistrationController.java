package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Controller
public class RegistrationController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
         return "redirect:/login#register";
    }

    @PostMapping("/register")
    @Transactional
    public String processRegistration(@RequestParam Map<String, String> formData, RedirectAttributes redirectAttributes) {

        String nombre = formData.get("nombre");
        String apellido = formData.get("apellido");
        String correo = formData.get("correoElectronico");
        String telefono = formData.get("telefono");
        String password = formData.get("password");
        String confirmPassword = formData.get("confirmPassword");
        String dni = formData.get("documentoDni");
        String perfil = formData.get("perfilExperiencia");

        // Validación 
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank() ||
            correo == null || correo.isBlank() || telefono == null || telefono.isBlank() ||
            password == null || password.isBlank()) {
            redirectAttributes.addFlashAttribute("registrationError", "Los campos marcados con * son obligatorios.");
            return "redirect:/login#register";
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("registrationError", "Las contraseñas no coinciden.");
            redirectAttributes.addFlashAttribute("formData", formData);
            return "redirect:/login#register";
        }

        if (usuarioRepository.existsByCorreoElectronico(correo)) {
            redirectAttributes.addFlashAttribute("registrationError", "El correo electrónico ya está registrado.");
            redirectAttributes.addFlashAttribute("formData", formData);
            return "redirect:/login#register";
        }

        // Validacion de telefono
        if (!telefono.matches("^\\+51[0-9]{9}$")) {
            redirectAttributes.addFlashAttribute("registrationError", "El formato del teléfono es inválido. Debe empezar con +51 seguido de 9 dígitos.");
            redirectAttributes.addFlashAttribute("formData", formData);
            return "redirect:/login#register";
        }

        if (usuarioRepository.existsByTelefono(telefono)) {
            redirectAttributes.addFlashAttribute("registrationError", "El número de teléfono ya está registrado.");
            redirectAttributes.addFlashAttribute("formData", formData);
            return "redirect:/login#register";
        }

        // Creacion del Usuario
        UsuarioModel nuevoUsuario = new UsuarioModel();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreoElectronico(correo);
        nuevoUsuario.setTelefono(telefono);
        nuevoUsuario.setContrasenaHash(passwordEncoder.encode(password));
        nuevoUsuario.setRol(UsuarioModel.Rol.PARTICIPANTE);

        // Campos opcionales
        if (dni != null && !dni.isBlank()) {
             if (usuarioRepository.existsByDocumentoDni(dni)) {
                redirectAttributes.addFlashAttribute("registrationError", "El DNI ya está registrado.");
                redirectAttributes.addFlashAttribute("formData", formData);
                return "redirect:/login#register";
            }
            nuevoUsuario.setDocumentoDni(dni);
        }
        if (perfil != null && !perfil.isBlank()) {
            nuevoUsuario.setPerfilExperiencia(perfil);
        }

        // Guardar en base de datos 
        try {
            usuarioRepository.save(nuevoUsuario);
            redirectAttributes.addFlashAttribute("registrationSuccess", "¡Cuenta creada con éxito! Ahora puedes iniciar sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registrationError", "Ocurrió un error al crear la cuenta. Inténtalo de nuevo.");
            redirectAttributes.addFlashAttribute("formData", formData);
            return "redirect:/login#register";
        }
    }
}
