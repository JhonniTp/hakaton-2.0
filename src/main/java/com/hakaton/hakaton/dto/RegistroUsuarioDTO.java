package com.hakaton.hakaton.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroUsuarioDTO {

  @NotBlank(message = "El nombre es obligatorio")
  @Size(max = 100, message = "El nombre no puede exceder los 100 caracteres")
  private String nombre;

  @NotBlank(message = "El apellido es obligatorio")
  @Size(max = 100, message = "El apellido no puede exceder los 100 caracteres")
  private String apellido;

  @NotBlank(message = "El correo electrónico es obligatorio")
  @Email(message = "El formato del correo electrónico no es válido")
  private String correoElectronico;

  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
  private String password;

  @NotBlank(message = "Debe confirmar la contraseña")
  private String confirmPassword;

  // Campos opcionales del formulario
  private String documentoDni; // studentNumber en el formulario
  private String telefono;
  private String perfilExperiencia; // Combinación de faculty + career

  public boolean passwordsCoinciden() {
    return password != null && password.equals(confirmPassword);
  }
}
