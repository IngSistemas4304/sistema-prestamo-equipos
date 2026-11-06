package com.uniajc.sistemaprestamos.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniajc.sistemaprestamos.entity.Solicitud;
import com.uniajc.sistemaprestamos.entity.DecisionSolicitud;
import com.uniajc.sistemaprestamos.repository.EquipoRepository;
import com.uniajc.sistemaprestamos.service.SolicitudService;
import com.uniajc.sistemaprestamos.service.DecisionSolicitudService;

@Controller
public class SolicitudWebController {

    private final SolicitudService solicitudService;
    private final EquipoRepository equipoRepository;
    private final DecisionSolicitudService decisionService;

    public SolicitudWebController(
            SolicitudService solicitudService,
            EquipoRepository equipoRepository,
            DecisionSolicitudService decisionService) {

        this.solicitudService = solicitudService;
        this.equipoRepository = equipoRepository;
        this.decisionService = decisionService;
    }

    @GetMapping("/solicitudes/nueva")
    public String mostrarFormulario(Model model) {

        model.addAttribute("equipos",
                equipoRepository.findByEstadoIgnoreCase("DISPONIBLE"));

        return "solicitudes";
    }

    @GetMapping("/solicitudes/mis-solicitudes")
    public String mostrarMisSolicitudes(
            HttpSession session, Model model) {

        Integer idUsuario = (Integer) session.getAttribute("idUsuario");

        List<Solicitud> solicitudes =
                solicitudService.consultarPorUsuario(idUsuario);

        Map<Long, List<DecisionSolicitud>> historial = new LinkedHashMap<>();

        for (Solicitud solicitud : solicitudes) {
            historial.put(
                    solicitud.getIdSolicitud(),
                    decisionService.consultarDecisiones(
                            solicitud.getIdSolicitud())
            );
        }

        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("historial", historial);

        return "mis-solicitudes";
    }

    @GetMapping("/solicitudes/pendientes/vista")
    public String mostrarSolicitudesPendientes(Model model) {

        model.addAttribute("solicitudes",
                solicitudService.consultarPendientes());

        return "solicitudes-pendientes";
    }

    @PostMapping("/solicitudes/crear")
    public String crearSolicitud(
            @RequestParam Long idEquipo,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaInicio,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaFin,
            @RequestParam String motivo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Integer idUsuario = (Integer) session.getAttribute("idUsuario");

            Solicitud solicitud = new Solicitud();
            solicitud.setIdUsuario(idUsuario);
            solicitud.setIdEquipo(idEquipo);
            solicitud.setFechaInicio(fechaInicio);
            solicitud.setFechaFin(fechaFin);
            solicitud.setMotivo(motivo);

            solicitudService.registrarSolicitud(solicitud);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Solicitud registrada correctamente");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/solicitudes/mis-solicitudes";
    }

    @PostMapping("/solicitudes/cancelar")
    public String cancelarSolicitud(
            @RequestParam Long idSolicitud,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Integer idUsuario = (Integer) session.getAttribute("idUsuario");

            solicitudService.cancelarSolicitud(idSolicitud, idUsuario);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "Solicitud cancelada correctamente");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/solicitudes/mis-solicitudes";
    }
}
