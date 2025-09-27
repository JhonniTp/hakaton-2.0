package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(max = 100, message = "El nombre de la categoría no puede exceder los 100 caracteres")
    @Column(name = "nombre_categoria", nullable = false, length = 100, unique = true)
    private String nombreCategoria;


    public CategoriaModel(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
}
