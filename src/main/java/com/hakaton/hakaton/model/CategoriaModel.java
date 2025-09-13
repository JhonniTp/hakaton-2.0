package com.hakaton.hakaton.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "Categorias")
@Getter
@Setter
public class CategoriaModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;
    
    @Column(name = "nombre_categoria", nullable = false, unique = true, length = 100)
    private String nombreCategoria;
    
    // Relación con hackatones
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HackatonModel> hackatones;
    
    // Constructor por defecto
    public CategoriaModel() {
    }
    
    // Constructor con parámetros
    public CategoriaModel(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }
}