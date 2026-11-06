package com.uniajc.sistemaprestamos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.uniajc.sistemaprestamos.dto.*;
import com.uniajc.sistemaprestamos.entity.*;
import com.uniajc.sistemaprestamos.service.PrestamoService;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    // REGISTRAR ENTREGA

    @PostMapping("/entregas")
    public ResponseEntity<Prestamo> registrarEntrega(
            @Valid @RequestBody RegistrarPrestamoRequest datos) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prestamoService.registrarEntrega(datos));
    }


    @PostMapping("/{id}/devoluciones")
    public ResponseEntity<Prestamo> registrarDevolucion(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarDevolucionRequest datos) {

        return ResponseEntity.ok(
                prestamoService.registrarDevolucion(id, datos));
    }


    @PostMapping("/novedades")
    public ResponseEntity<Novedad> registrarNovedad(
            @Valid @RequestBody RegistrarNovedadRequest datos) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(prestamoService.registrarNovedad(datos));
    }


    @GetMapping("/activos")
    public List<Prestamo> listarActivos() {

        return prestamoService.listarActivos();
    }


    @GetMapping("/historial")
    public List<Prestamo> listarHistorial() {

        return prestamoService.listarHistorial();
    }


    @GetMapping("/vencidos")
    public List<Prestamo> listarVencidos() {

        return prestamoService.listarVencidos();
    }


    @GetMapping("/usuario/{idUsuario}")
    public List<Prestamo> listarPorUsuario(
            @PathVariable Long idUsuario) {

        return prestamoService.listarPorUsuario(idUsuario);
    }


    @GetMapping("/{id}/novedades")
    public List<Novedad> listarNovedades(
            @PathVariable Long id) {

        return prestamoService.listarNovedades(id);
    }



    @GetMapping("/reportes")
    public Map<String, Long> obtenerReporte() {

        return prestamoService.obtenerReporte();
    }

    

    @GetMapping("/auditoria")
    public List<Auditoria> listarAuditoria() {

        return prestamoService.listarAuditoria();
    }
}



@RestControllerAdvice
class PrestamoExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> manejarDatosInvalidos(
            IllegalArgumentException ex) {

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> manejarEstadoInvalido(
            IllegalStateException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ex.getMessage());
    }
}