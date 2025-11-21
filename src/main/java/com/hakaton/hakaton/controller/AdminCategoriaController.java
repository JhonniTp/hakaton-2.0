package com.hakaton.hakaton.controller;

import com.hakaton.hakaton.model.CategoriaModel;
import com.hakaton.hakaton.repository.CategoriaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categorias")
public class AdminCategoriaController {

    private final CategoriaRepository categoriaRepository;

    public AdminCategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaModel>> obtenerTodasLasCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAll());
    }
}
