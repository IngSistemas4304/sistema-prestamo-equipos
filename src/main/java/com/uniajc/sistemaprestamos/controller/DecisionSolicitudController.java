
package com.uniajc.sistemaprestamos.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniajc.sistemaprestamos.service.DecisionSolicitudService;

@Controller
public class DecisionSolicitudController {

    private final DecisionSolicitudService decisionService;

    public DecisionSolicitudController(
            DecisionSolicitudService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/decisiones/{idSolicitud}")
    public String registrarDecision(
            @PathVariable Long idSolicitud,
            @RequestParam String decision,
            @RequestParam(required = false) String observacion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            Integer idResponsable =
                    (Integer) session.getAttribute("idUsuario");

            decisionService.registrarDecision(
                    idSolicitud,
                    idResponsable,
                    decision,
                    observacion
            );

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Decisión registrada correctamente"
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/solicitudes/pendientes/vista";
    }
}
