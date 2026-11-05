package com.uniajc.sistemaprestamos.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uniajc.sistemaprestamos.entity.Solicitud;
import com.uniajc.sistemaprestamos.service.SolicitudService;

@RestController
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // Registrar una solicitud
    @PostMapping
    public Solicitud registrarSolicitud(@RequestBody Solicitud solicitud) {
        return solicitudService.registrarSolicitud(solicitud);
    }

    // Consultar todas las solicitudes
    @GetMapping
    public List<Solicitud> listarSolicitudes() {
        return solicitudService.listarSolicitudes();
    }

    // Consultar solicitudes pendientes
    @GetMapping("/pendientes")
    public List<Solicitud> consultarPendientes() {
        return solicitudService.consultarPendientes();
    }
    
    // Consultar solicitudes de un usuario
    @GetMapping("/usuario/{idUsuario}")
    public List<Solicitud> consultarPorUsuario(
            @org.springframework.web.bind.annotation.PathVariable Long idUsuario) {

        return solicitudService.consultarPorUsuario(idUsuario);
    }
    
    // Cancelar una solicitud
    @DeleteMapping("/{idSolicitud}/usuario/{idUsuario}")
    public Solicitud cancelarSolicitud(
            @org.springframework.web.bind.annotation.PathVariable Long idSolicitud,
            @org.springframework.web.bind.annotation.PathVariable Long idUsuario) {

        return solicitudService.cancelarSolicitud(idSolicitud, idUsuario);
    }
}
