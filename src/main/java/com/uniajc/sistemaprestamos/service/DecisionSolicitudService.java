package com.uniajc.sistemaprestamos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.uniajc.sistemaprestamos.entity.DecisionSolicitud;
import com.uniajc.sistemaprestamos.entity.Solicitud;
import com.uniajc.sistemaprestamos.repository.DecisionSolicitudRepository;
import com.uniajc.sistemaprestamos.repository.SolicitudRepository;

@Service
public class DecisionSolicitudService {

    private final DecisionSolicitudRepository decisionRepository;
    private final SolicitudRepository solicitudRepository;

    public DecisionSolicitudService(
            DecisionSolicitudRepository decisionRepository,
            SolicitudRepository solicitudRepository) {
        this.decisionRepository = decisionRepository;
        this.solicitudRepository = solicitudRepository;
    }

    // Aprobar o rechazar una solicitud
    public DecisionSolicitud registrarDecision(
            Long idSolicitud,
            Integer idResponsable,
            String decision,
            String observacion) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La solicitud no existe"));

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalArgumentException(
                    "La solicitud ya fue procesada");
        }

        if (!"APROBADA".equals(decision)
                && !"RECHAZADA".equals(decision)) {
            throw new IllegalArgumentException(
                    "La decisión debe ser APROBADA o RECHAZADA");
        }

        DecisionSolicitud nuevaDecision = new DecisionSolicitud();
        nuevaDecision.setIdSolicitud(idSolicitud);
        nuevaDecision.setIdResponsable(idResponsable);
        nuevaDecision.setDecision(decision);
        nuevaDecision.setObservacion(observacion);
        nuevaDecision.setFechaDecision(LocalDateTime.now());

        solicitud.setEstado(decision);
        solicitudRepository.save(solicitud);

        return decisionRepository.save(nuevaDecision);
    }

    // Consultar decisiones de una solicitud
    public List<DecisionSolicitud> consultarDecisiones(Long idSolicitud) {
        return decisionRepository.findByIdSolicitud(idSolicitud);
    }
}
