package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping(value = "/google", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String googleId = payload.get("googleId");
        String email = payload.get("correoElectronico");
        String nombre = payload.get("nombre");
        String apellido = payload.get("apellido");

        UsuarioModel user = usuarioRepository.findByGoogleId(googleId)
                .orElseGet(() -> usuarioRepository.findByCorreoElectronico(email)
                        .map(existingUser -> {
                            existingUser.setGoogleId(googleId);
                            return usuarioRepository.save(existingUser);
                        })
                        .orElseGet(() -> {
                            UsuarioModel newUser = new UsuarioModel();
                            newUser.setGoogleId(googleId);
                            newUser.setCorreoElectronico(email);
                            newUser.setNombre(nombre);
                            newUser.setApellido(apellido);
                            newUser.setRol(UsuarioModel.Rol.PARTICIPANTE);
                            return usuarioRepository.save(newUser);
                        }));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        String redirectUrl = getRedirectUrlForRole(user.getRol());
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }

    private String getRedirectUrlForRole(UsuarioModel.Rol rol) {
        if (rol == null) return "/login?error=true";
        switch (rol) {
            case ADMINISTRADOR: return "/admin/dashboard";
            case JURADO: return "/jurado/dashboard";
            case PARTICIPANTE: return "/participante/dashboard";
            default: return "/";
        }
    }
}

