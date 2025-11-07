package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.dto.UsuarioDTO;
import com.hakaton.hakaton.model.UsuarioModel;
import com.hakaton.hakaton.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodosLosUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.obtenerTodosLosUsuarios()
                .stream()
                .map(UsuarioDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Long id) {
        try {
            UsuarioModel usuario = usuarioService.obtenerUsuarioPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            return ResponseEntity.ok(new UsuarioDTO(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            if (usuarioDTO.getContrasena() == null || usuarioDTO.getContrasena().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(crearRespuestaError("La contraseña es obligatoria al crear un usuario"));
            }
            
            UsuarioModel nuevoUsuario = usuarioService.crearUsuario(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(crearRespuestaExito("Usuario creado exitosamente", new UsuarioDTO(nuevoUsuario)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al crear el usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, 
                                               @Valid @RequestBody UsuarioDTO usuarioDTO) {
        try {
            UsuarioModel usuarioActualizado = usuarioService.actualizarUsuario(id, usuarioDTO);
            return ResponseEntity.ok(crearRespuestaExito("Usuario actualizado exitosamente", 
                    new UsuarioDTO(usuarioActualizado)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al actualizar el usuario: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok(crearRespuestaExito("Usuario eliminado exitosamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(crearRespuestaError(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearRespuestaError("Error al eliminar el usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/verificar-correo")
    public ResponseEntity<Map<String, Boolean>> verificarCorreo(@RequestParam String correo) {
        boolean existe = usuarioService.existeCorreoElectronico(correo);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("existe", existe);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/verificar-telefono")
    public ResponseEntity<Map<String, Boolean>> verificarTelefono(@RequestParam String telefono) {
        boolean existe = usuarioService.existeTelefono(telefono);
        Map<String, Boolean> respuesta = new HashMap<>();
        respuesta.put("existe", existe);
        return ResponseEntity.ok(respuesta);
    }

    private Map<String, Object> crearRespuestaExito(String mensaje, Object data) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", true);
        respuesta.put("mensaje", mensaje);
        if (data != null) {
            respuesta.put("data", data);
        }
        return respuesta;
    }

    private Map<String, Object> crearRespuestaError(String mensaje) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("success", false);
        respuesta.put("mensaje", mensaje);
        return respuesta;
    }
}
