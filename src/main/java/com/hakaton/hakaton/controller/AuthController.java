package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;

    public AuthController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String googleId = payload.get("googleId");
        String email = payload.get("correoElectronico");
        String nombre = payload.get("nombre");
        String apellido = payload.get("apellido");

        Optional<UsuarioModel> existingUser = usuarioRepository.findByGoogleId(googleId);

        if (existingUser.isPresent()) {
            UsuarioModel user = existingUser.get();
            return ResponseEntity.ok(Map.of(
                "message", "Login successful", 
                "userId", user.getIdUsuario(),
                "rol", user.getRol().name()
            ));
        } else {
            UsuarioModel newUser = new UsuarioModel();
            newUser.setGoogleId(googleId);
            newUser.setCorreoElectronico(email);
            newUser.setNombre(nombre);
            newUser.setApellido(apellido);
            newUser.setRol(UsuarioModel.Rol.PARTICIPANTE);
            
            usuarioRepository.save(newUser);
            
            return new ResponseEntity<>(Map.of(
                "message", "User registered successfully", 
                "userId", newUser.getIdUsuario(),
                "rol", newUser.getRol().name()
            ), HttpStatus.CREATED);
        }
    }
}
