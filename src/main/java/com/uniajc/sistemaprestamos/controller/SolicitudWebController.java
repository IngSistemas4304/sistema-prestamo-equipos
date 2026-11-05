package com.uniajc.sistemaprestamos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uniajc.sistemaprestamos.service.SolicitudService;
import com.uniajc.sistemaprestamos.repository.EquipoRepository;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import com.uniajc.sistemaprestamos.entity.Solicitud;

@Controller
public class SolicitudWebController {

    private final SolicitudService solicitudService;
    private final EquipoRepository equipoRepository;

    public SolicitudWebController(
    		SolicitudService solicitudService,
            EquipoRepository equipoRepository) {
        this.solicitudService = solicitudService;
        this.equipoRepository = equipoRepository;
    }

    @GetMapping("/solicitudes/nueva")
    public String mostrarFormulario(Model model) {

        model.addAttribute("equipos",
                equipoRepository.findByEstadoIgnoreCase("DISPONIBLE"));

        return "solicitudes";
    }

    @GetMapping("/solicitudes/mis-solicitudes")
    public String mostrarMisSolicitudes(
            @RequestParam Long idUsuario, Model model) {

        model.addAttribute("solicitudes",
                solicitudService.consultarPorUsuario(idUsuario));

        return "mis-solicitudes";
    }

    @GetMapping("/solicitudes/pendientes/vista")
    public String mostrarSolicitudesPendientes(Model model) {

        model.addAttribute("solicitudes",
                solicitudService.consultarPendientes());

        return "solicitudes-pendientes";
    }
    
    // Registrar una nueva solicitud desde el formulario
    @PostMapping("/solicitudes/crear")
    public String crearSolicitud(
            @RequestParam Long idUsuario,
            @RequestParam Long idEquipo,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin,
            @RequestParam String motivo) {

        Solicitud solicitud = new Solicitud();

        solicitud.setIdUsuario(idUsuario);
        solicitud.setIdEquipo(idEquipo);
        solicitud.setFechaInicio(fechaInicio);
        solicitud.setFechaFin(fechaFin);
        solicitud.setMotivo(motivo);

        solicitudService.registrarSolicitud(solicitud);

        return "redirect:/solicitudes/mis-solicitudes?idUsuario=" + idUsuario;
    }
}
