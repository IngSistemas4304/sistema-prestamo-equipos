
package com.uniajc.sistemaprestamos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uniajc.sistemaprestamos.entity.CategoriaEquipo;
import com.uniajc.sistemaprestamos.service.CategoriaEquipoService;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaEquipoController {

    private final CategoriaEquipoService categoriaService;

    public CategoriaEquipoController(CategoriaEquipoService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaEquipo>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaEquipo> buscarCategoria(@PathVariable Long id) {
        CategoriaEquipo categoria = categoriaService.buscarCategoria(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<CategoriaEquipo> registrarCategoria(
            @RequestBody CategoriaEquipo categoria) {

        CategoriaEquipo nueva = categoriaService.guardarCategoria(categoria);

        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody CategoriaEquipo categoria) {

        CategoriaEquipo existente = categoriaService.buscarCategoria(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        if ("INACTIVO".equalsIgnoreCase(categoria.getEstado())
                && !"INACTIVO".equalsIgnoreCase(existente.getEstado())
                && categoriaService.tieneEquiposAsociados(id)) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede inactivar la categoria porque tiene equipos asociados.");
        }

        categoria.setIdCategoria(id);

        return ResponseEntity.ok(categoriaService.guardarCategoria(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCategoria(@PathVariable Long id) {

        CategoriaEquipo categoria = categoriaService.buscarCategoria(id);

        if (categoria == null) {
            return ResponseEntity.notFound().build();
        }

        if (categoriaService.tieneEquiposAsociados(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar la categoria porque tiene equipos asociados.");
        }

        categoriaService.eliminarCategoria(id);

        return ResponseEntity.ok("Categoria eliminada correctamente.");
    }
}
