package com.hakaton.hakaton.dto;

import com.hakaton.hakaton.model.UsuarioModel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    
    private Long idUsuario;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
    private String apellido;
    
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Size(max = 255, message = "El correo no puede exceder los 255 caracteres")
    private String correoElectronico;
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;
    
    @Size(max = 255, message = "El DNI no puede exceder los 255 caracteres")
    private String documentoDni;
    
    @Size(max = 255, message = "El teléfono no puede exceder los 255 caracteres")
    private String telefono;
    
    @NotNull(message = "El rol es obligatorio")
    private UsuarioModel.Rol rol;
    
    private String perfilExperiencia;
    
    private String urlCodigoQr;
    
    public UsuarioDTO(UsuarioModel usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.correoElectronico = usuario.getCorreoElectronico();
        this.documentoDni = usuario.getDocumentoDni();
        this.telefono = usuario.getTelefono();
        this.rol = usuario.getRol();
        this.perfilExperiencia = usuario.getPerfilExperiencia();
        this.urlCodigoQr = usuario.getUrlCodigoQr();
    }
    
    public UsuarioModel toUsuarioModel() {
        UsuarioModel usuario = new UsuarioModel();
        usuario.setIdUsuario(this.idUsuario);
        usuario.setNombre(this.nombre);
        usuario.setApellido(this.apellido);
        usuario.setCorreoElectronico(this.correoElectronico);
        usuario.setDocumentoDni(this.documentoDni);
        usuario.setTelefono(this.telefono);
        usuario.setRol(this.rol);
        usuario.setPerfilExperiencia(this.perfilExperiencia);
        usuario.setUrlCodigoQr(this.urlCodigoQr);
        return usuario;
    }
}
