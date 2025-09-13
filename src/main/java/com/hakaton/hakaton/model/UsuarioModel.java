package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "Usuarios")
@Getter
@Setter
public class UsuarioModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;
    
    @Column(name = "correo_electronico", nullable = false, unique = true, length = 255)
    private String correoElectronico;
    
    @Column(name = "documento_dni", length = 255)
    private String documentoDni;
    
    @Column(name = "telefono", length = 255)
    private String telefono;
    
    @Column(name = "google_id", unique = true, length = 255)
    private String googleId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, columnDefinition = "ENUM('administrador', 'participante', 'jurado')")
    private Rol rol = Rol.participante;
    
    @Column(name = "perfil_experiencia", columnDefinition = "TEXT")
    private String perfilExperiencia;
    
    @Column(name = "url_codigo_qr", length = 255)
    private String urlCodigoQr;
    
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
    // Enumeración para los roles
    public enum Rol {
        administrador, participante, jurado
    }
    
    // Constructor por defecto
    public UsuarioModel() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Constructor con parámetros básicos
    public UsuarioModel(String nombre, String apellido, String correoElectronico, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = correoElectronico;
        this.rol = rol;
        this.fechaCreacion = LocalDateTime.now();
    }
}