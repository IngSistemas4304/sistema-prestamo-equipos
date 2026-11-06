
package com.uniajc.sistemaprestamos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniajc.sistemaprestamos.dto.RegistrarPrestamoRequest;
import com.uniajc.sistemaprestamos.entity.DecisionSolicitud;
import com.uniajc.sistemaprestamos.entity.Solicitud;
import com.uniajc.sistemaprestamos.repository.DecisionSolicitudRepository;
import com.uniajc.sistemaprestamos.repository.SolicitudRepository;

@Service
public class DecisionSolicitudService {

    private final DecisionSolicitudRepository decisionRepository;
    private final SolicitudRepository solicitudRepository;
    private final PrestamoService prestamoService;

    public DecisionSolicitudService(
            DecisionSolicitudRepository decisionRepository,
            SolicitudRepository solicitudRepository,
            PrestamoService prestamoService) {

        this.decisionRepository = decisionRepository;
        this.solicitudRepository = solicitudRepository;
        this.prestamoService = prestamoService;
    }

    // =========================================
    // APROBAR O RECHAZAR UNA SOLICITUD
    // =========================================

    @Transactional
    public DecisionSolicitud registrarDecision(
            Long idSolicitud,
            Integer idResponsable,
            String decision,
            String observacion) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La solicitud no existe."));

        if (!"PENDIENTE".equalsIgnoreCase(solicitud.getEstado())) {

            throw new IllegalArgumentException(
                    "La solicitud ya fue procesada.");
        }

        if (!"APROBADA".equalsIgnoreCase(decision)
                && !"RECHAZADA".equalsIgnoreCase(decision)) {

            throw new IllegalArgumentException(
                    "La decisión debe ser APROBADA o RECHAZADA.");
        }

        DecisionSolicitud nuevaDecision = new DecisionSolicitud();

        nuevaDecision.setIdSolicitud(idSolicitud);
        nuevaDecision.setIdResponsable(idResponsable);
        nuevaDecision.setDecision(decision);
        nuevaDecision.setObservacion(observacion);
        nuevaDecision.setFechaDecision(LocalDateTime.now());

        solicitud.setEstado(decision);

        solicitudRepository.save(solicitud);

        // Si la solicitud fue aprobada, crear el préstamo automáticamente
        if ("APROBADA".equalsIgnoreCase(decision)) {

            RegistrarPrestamoRequest datosPrestamo =
                    new RegistrarPrestamoRequest(
                            solicitud.getIdSolicitud(),
                            solicitud.getIdEquipo(),
                            solicitud.getFechaInicio(),
                            solicitud.getFechaFin(),
                            "Préstamo generado automáticamente al aprobar la solicitud."
                    );

            prestamoService.registrarEntrega(datosPrestamo);
        }

        // Guardar la decisión del responsable
        return decisionRepository.save(nuevaDecision);
    }

    // =========================================
    // CONSULTAR DECISIONES DE UNA SOLICITUD
    // =========================================

    public List<DecisionSolicitud> consultarDecisiones(
            Long idSolicitud) {

        return decisionRepository.findByIdSolicitud(idSolicitud);
    }
}
