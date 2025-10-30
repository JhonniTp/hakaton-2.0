package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.RegistroUsuarioDTO;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import com.hakaton.hakaton.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AuthController(UsuarioRepository usuarioRepository, UsuarioService usuarioService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
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

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Valid @RequestBody RegistroUsuarioDTO registroDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Validar errores de validación
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()));
            response.put("success", false);
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Registrar usuario
            UsuarioModel nuevoUsuario = usuarioService.registrarUsuario(registroDTO);

            // Autenticar automáticamente al usuario después del registro
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    nuevoUsuario, null, nuevoUsuario.getAuthorities());

            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            String redirectUrl = getRedirectUrlForRole(nuevoUsuario.getRol());

            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("redirectUrl", redirectUrl);
            response.put("usuario", Map.of(
                    "nombre", nuevoUsuario.getNombre(),
                    "apellido", nuevoUsuario.getApellido(),
                    "correo", nuevoUsuario.getCorreoElectronico(),
                    "rol", nuevoUsuario.getRol().name()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al registrar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping(value = "/check-email", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean existe = usuarioService.existeCorreoElectronico(email);
        return ResponseEntity.ok(Map.of("existe", existe));
    }

    private String getRedirectUrlForRole(UsuarioModel.Rol rol) {
        if (rol == null)
            return "/login?error=true";
        switch (rol) {
            case ADMINISTRADOR:
                return "/admin/dashboard";
            case JURADO:
                return "/jurado/dashboard";
            case PARTICIPANTE:
                return "/participante/dashboard";
            default:
                return "/";
        }
    }
}
