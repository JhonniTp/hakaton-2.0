package com.hakaton.hakaton.model;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class UsuarioModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 255, message = "El correo no puede exceder los 255 caracteres")
    @Column(name = "correo_electronico", nullable = false, unique = true)
    private String correoElectronico;

    @Size(max = 255, message = "El hash de la contraseña no puede exceder los 255 caracteres")
    @Column(name = "contrasena_hash", length = 255, nullable = true)
    private String contrasenaHash;

    @Size(max = 255, message = "El DNI no puede exceder los 255 caracteres")
    @Column(name = "documento_dni", length = 255)
    private String documentoDni;

    @Size(max = 255, message = "El teléfono no puede exceder los 255 caracteres")
    @Column(name = "telefono", length = 255, unique = true)
    private String telefono;

    @Column(name = "google_id", unique = true, length = 255, nullable = true)
    private String googleId;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Rol rol = Rol.PARTICIPANTE;

    @Column(name = "perfil_experiencia", columnDefinition = "TEXT")
    private String perfilExperiencia;

    @Column(name = "url_codigo_qr", length = 255)
    private String urlCodigoQr;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public enum Rol {
        ADMINISTRADOR, PARTICIPANTE, JURADO
    }

    public UsuarioModel(String nombre, String apellido, String correoElectronico, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correoElectronico = correoElectronico;
        this.rol = rol;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    @Override
    public String getPassword() {
        return this.contrasenaHash;
    }

    @Override
    public String getUsername() {
        return this.correoElectronico;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Transient
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Transient
    public String getIniciales() {
        try {
            if (nombre != null && !nombre.isBlank() && apellido != null && !apellido.isBlank()) {
                return ("" + Character.toUpperCase(nombre.charAt(0)) + Character.toUpperCase(apellido.charAt(0)));
            }

            if (correoElectronico != null && correoElectronico.contains("@")) {
                String localPart = correoElectronico.split("@")[0];
                String[] partes = localPart.split("[\\.\\-_]");
                StringBuilder sb = new StringBuilder();
                for (String p : partes) {
                    if (!p.isBlank()) {
                        sb.append(Character.toUpperCase(p.charAt(0)));
                    }
                    if (sb.length() >= 2) {
                        break;
                    }
                }
                if (sb.length() > 0) {
                    return sb.length() == 1 ? sb.toString() : sb.toString().substring(0, 2);
                }
            }
        } catch (Exception e) {
            return "U";
        }
        return "U";
    }

    public boolean esAdministrador() {
        return Rol.ADMINISTRADOR.equals(this.rol);
    }

    public boolean esJurado() {
        return Rol.JURADO.equals(this.rol);
    }

    public boolean esParticipante() {
        return Rol.PARTICIPANTE.equals(this.rol);
    }
}
