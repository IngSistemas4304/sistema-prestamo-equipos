package com.uniajc.sistemaprestamos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uniajc.sistemaprestamos.entity.Equipo;
import com.uniajc.sistemaprestamos.service.EquipoService;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final EquipoService equipoService;

    public EquipoController(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @GetMapping
    public ResponseEntity<List<Equipo>> listarEquipos() {
        return ResponseEntity.ok(equipoService.listarEquipos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Equipo> buscarEquipo(@PathVariable Long id) {
        Equipo equipo = equipoService.buscarEquipo(id);

        if (equipo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(equipo);
    }

    @PostMapping
    public ResponseEntity<?> registrarEquipo(@RequestBody Equipo equipo) {

        String error = equipoService.validarDatos(equipo);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        if (equipoService.existeCodigo(equipo.getCodigo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe un equipo registrado con ese codigo.");
        }

        Equipo nuevo = equipoService.guardarEquipo(equipo);

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEquipo(
            @PathVariable Long id,
            @RequestBody Equipo equipo) {

        Equipo existente = equipoService.buscarEquipo(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        String error = equipoService.validarDatos(equipo);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        if (equipoService.existeCodigoEnOtroEquipo(equipo.getCodigo(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Ya existe otro equipo registrado con ese codigo.");
        }

        equipo.setIdEquipo(id);

        return ResponseEntity.ok(equipoService.guardarEquipo(equipo));
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Equipo>> filtrarPorCategoria(
            @PathVariable Long idCategoria) {

        return ResponseEntity.ok(
                equipoService.filtrarPorCategoria(idCategoria));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Equipo>> filtrarPorEstado(
            @PathVariable String estado) {

        return ResponseEntity.ok(
                equipoService.filtrarPorEstado(estado));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Equipo>> buscarPorTexto(
            @RequestParam String texto) {

        if (texto == null || texto.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                equipoService.buscarPorTexto(texto.trim()));
    }

    @PatchMapping("/{id}/inactivar")
    public ResponseEntity<?> inactivarEquipo(@PathVariable Long id) {

        try {
            Equipo equipo = equipoService.inactivarEquipo(id);

            if (equipo == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(equipo);

        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ex.getMessage());
        }
    }

    @GetMapping("/{id}/puede-prestarse")
    public ResponseEntity<Boolean> puedePrestarse(@PathVariable Long id) {

        if (equipoService.buscarEquipo(id) == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(equipoService.puedePrestarse(id));
    }
}