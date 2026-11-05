package com.uniajc.sistemaprestamos.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uniajc.sistemaprestamos.entity.CategoriaEquipo;
import com.uniajc.sistemaprestamos.repository.CategoriaEquipoRepository;
import com.uniajc.sistemaprestamos.repository.EquipoRepository;

@Service
public class CategoriaEquipoService {

    private final CategoriaEquipoRepository categoriaRepository;
    private final EquipoRepository equipoRepository;

    public CategoriaEquipoService(
            CategoriaEquipoRepository categoriaRepository,
            EquipoRepository equipoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.equipoRepository = equipoRepository;
    }

    public List<CategoriaEquipo> listarCategorias() {
        return categoriaRepository.findAll();
    }

    public CategoriaEquipo buscarCategoria(Long id) {
        return categoriaRepository.findById(id).orElse(null);
    }

    public CategoriaEquipo guardarCategoria(CategoriaEquipo categoria) {
        return categoriaRepository.save(categoria);
    }

    public boolean tieneEquiposAsociados(Long idCategoria) {
        return !equipoRepository.findByCategoria_IdCategoria(idCategoria).isEmpty();
    }

    public void eliminarCategoria(Long id) {
        categoriaRepository.deleteById(id);
    }
}