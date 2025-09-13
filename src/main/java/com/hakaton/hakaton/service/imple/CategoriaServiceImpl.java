package com.hakaton.hakaton.service.imple;

import com.hakaton.hakaton.model.CategoriaModel;
import com.hakaton.hakaton.repository.CategoriaRepository;
import com.hakaton.hakaton.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public CategoriaModel crearCategoria(CategoriaModel categoria) {
        // Validar que el nombre de la categoría no exista
        if (categoriaRepository.existsByNombreCategoria(categoria.getNombreCategoria())) {
            throw new RuntimeException("Ya existe una categoría con este nombre: " + categoria.getNombreCategoria());
        }
        
        return categoriaRepository.save(categoria);
    }

    @Override
    public List<CategoriaModel> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public Optional<CategoriaModel> obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    @Override
    public Optional<CategoriaModel> obtenerCategoriaPorNombre(String nombre) {
        return categoriaRepository.findByNombreCategoria(nombre);
    }

    @Override
    public List<CategoriaModel> buscarCategoriasPorNombre(String nombre) {
        return categoriaRepository.buscarPorNombre(nombre);
    }

    @Override
    public CategoriaModel actualizarCategoria(Long id, CategoriaModel categoriaActualizada) {
        return categoriaRepository.findById(id)
                .map(categoriaExistente -> {
                    // Validar que el nuevo nombre no esté en uso por otra categoría
                    if (!categoriaExistente.getNombreCategoria().equals(categoriaActualizada.getNombreCategoria()) &&
                        categoriaRepository.existsByNombreCategoria(categoriaActualizada.getNombreCategoria())) {
                        throw new RuntimeException("Ya existe una categoría con este nombre: " + categoriaActualizada.getNombreCategoria());
                    }
                    
                    // Actualizar campos
                    categoriaExistente.setNombreCategoria(categoriaActualizada.getNombreCategoria());
                    
                    return categoriaRepository.save(categoriaExistente);
                })
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
    }

    @Override
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    @Override
    public boolean existeCategoriaConNombre(String nombre) {
        return categoriaRepository.existsByNombreCategoria(nombre);
    }

    @Override
    public List<Object[]> obtenerCategoriasMasPopulares() {
        return categoriaRepository.findCategoriasMasPopulares();
    }
}