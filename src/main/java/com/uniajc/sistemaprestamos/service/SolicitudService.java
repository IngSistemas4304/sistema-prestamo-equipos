package com.uniajc.sistemaprestamos.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.uniajc.sistemaprestamos.entity.Equipo;
import com.uniajc.sistemaprestamos.entity.Solicitud;
import com.uniajc.sistemaprestamos.repository.EquipoRepository;
import com.uniajc.sistemaprestamos.repository.SolicitudRepository;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final EquipoRepository equipoRepository;

    public SolicitudService(
            SolicitudRepository solicitudRepository,
            EquipoRepository equipoRepository) {

        this.solicitudRepository = solicitudRepository;
        this.equipoRepository = equipoRepository;
    }

    // Registrar una solicitud
    public Solicitud registrarSolicitud(Solicitud solicitud) {

        if (solicitud.getFechaInicio() == null ||
            solicitud.getFechaFin() == null) {
            throw new IllegalArgumentException(
                    "Las fechas son obligatorias");
        }

        if (solicitud.getFechaInicio().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser anterior a hoy");
        }

        if (!solicitud.getFechaFin().isAfter(
                solicitud.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha de devolución debe ser posterior a la fecha de inicio");
        }

        if (solicitud.getIdUsuario() == null ||
            solicitud.getIdEquipo() == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el usuario y el equipo");
        }

        if (solicitud.getMotivo() == null ||
            solicitud.getMotivo().isBlank()) {
            throw new IllegalArgumentException(
                    "Debe escribir el motivo de la solicitud");
        }

        // Verificar que el equipo exista
        Equipo equipo = equipoRepository.findById(solicitud.getIdEquipo())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El equipo seleccionado no existe"));

        // Verificar que el equipo esté disponible
        if (!"DISPONIBLE".equalsIgnoreCase(equipo.getEstado())) {
            throw new IllegalArgumentException(
                    "El equipo no está disponible para solicitudes");
        }

        // Verificar solicitudes con fechas cruzadas
        boolean existeCruce = solicitudRepository
                .existsByIdEquipoAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        solicitud.getIdEquipo(),
                        List.of("PENDIENTE", "APROBADA"),
                        solicitud.getFechaFin(),
                        solicitud.getFechaInicio()
                );

        if (existeCruce) {
            throw new IllegalArgumentException(
                    "El equipo ya tiene una solicitud para esas fechas");
        }

        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setEstado("PENDIENTE");

        return solicitudRepository.save(solicitud);
    }

    // Consultar todas las solicitudes
    public List<Solicitud> listarSolicitudes() {
        return solicitudRepository.findAll();
    }

    // Consultar solicitudes de un usuario
    public List<Solicitud> consultarPorUsuario(Integer idUsuario) {
        return solicitudRepository.findByIdUsuario(idUsuario);
    }

    // Consultar solicitudes pendientes
    public List<Solicitud> consultarPendientes() {
        return solicitudRepository.findByEstado("PENDIENTE");
    }

    // Cancelar una solicitud pendiente
    public Solicitud cancelarSolicitud(Long idSolicitud, Integer idUsuario) {

        Solicitud solicitud = solicitudRepository.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La solicitud no existe"));

        if (!solicitud.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException(
                    "No puedes cancelar una solicitud de otro usuario");
        }

        if (!"PENDIENTE".equals(solicitud.getEstado())) {
            throw new IllegalArgumentException(
                    "Solo puedes cancelar solicitudes pendientes");
        }

        solicitud.setEstado("CANCELADA");

        return solicitudRepository.save(solicitud);
    }
}