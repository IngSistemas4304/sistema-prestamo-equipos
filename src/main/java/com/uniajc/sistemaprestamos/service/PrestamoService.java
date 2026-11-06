package com.uniajc.sistemaprestamos.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniajc.sistemaprestamos.dto.*;
import com.uniajc.sistemaprestamos.entity.*;
import com.uniajc.sistemaprestamos.repository.*;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final SolicitudRepository solicitudRepository;
    private final EquipoRepository equipoRepository;
    private final NovedadRepository novedadRepository;
    private final AuditoriaRepository auditoriaRepository;

    public PrestamoService(
            PrestamoRepository prestamoRepository,
            SolicitudRepository solicitudRepository,
            EquipoRepository equipoRepository,
            NovedadRepository novedadRepository,
            AuditoriaRepository auditoriaRepository) {

        this.prestamoRepository = prestamoRepository;
        this.solicitudRepository = solicitudRepository;
        this.equipoRepository = equipoRepository;
        this.novedadRepository = novedadRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    // =========================================
    // REGISTRAR ENTREGA
    // =========================================

    @Transactional
    public Prestamo registrarEntrega(RegistrarPrestamoRequest datos) {

        if (datos.fechaDevolucionPrevista()
                .isBefore(datos.fechaEntrega())) {

            throw new IllegalArgumentException(
                    "La fecha prevista no puede ser anterior a la fecha de entrega.");
        }

        // Consultar la solicitud original
        Solicitud solicitud = solicitudRepository
                .findById(datos.idSolicitud())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "La solicitud indicada no existe."));

        // Verificar que esté aprobada
        if (!"APROBADA".equalsIgnoreCase(solicitud.getEstado())) {

            throw new IllegalStateException(
                    "No se puede registrar el préstamo porque la solicitud no está aprobada.");
        }

        // Verificar que el equipo corresponda a la solicitud
        if (!solicitud.getIdEquipo().equals(datos.idEquipo())) {

            throw new IllegalArgumentException(
                    "El equipo seleccionado no corresponde a la solicitud aprobada.");
        }

        // Verificar la fecha de devolución
        if (!solicitud.getFechaFin()
                .equals(datos.fechaDevolucionPrevista())) {

            throw new IllegalArgumentException(
                    "La fecha de devolución debe coincidir con la fecha aprobada en la solicitud.");
        }

        // Evitar registrar dos préstamos para la misma solicitud
        if (prestamoRepository.existsByIdSolicitud(datos.idSolicitud())) {

            throw new IllegalStateException(
                    "Esta solicitud ya tiene un préstamo registrado.");
        }

        // Obtener el usuario directamente de la solicitud
        Long idUsuario = solicitud.getIdUsuario().longValue();

        // Verificar el equipo
        Equipo equipo = equipoRepository.findById(datos.idEquipo())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El equipo indicado no existe."));

        if (!"DISPONIBLE".equalsIgnoreCase(equipo.getEstado())) {

            throw new IllegalStateException(
                    "El equipo no está disponible para préstamo.");
        }

        // Crear préstamo
        Prestamo prestamo = new Prestamo();

        prestamo.setIdSolicitud(datos.idSolicitud());
        prestamo.setIdUsuario(idUsuario);
        prestamo.setEquipo(equipo);
        prestamo.setFechaEntrega(datos.fechaEntrega());
        prestamo.setFechaDevolucionPrevista(
                datos.fechaDevolucionPrevista());
        prestamo.setObservaciones(datos.observaciones());
        prestamo.setEstado("ACTIVO");

        // Actualizar estado del equipo
        equipo.setEstado("PRESTADO");
        equipoRepository.save(equipo);

        Prestamo guardado = prestamoRepository.save(prestamo);

        registrarAuditoria(
                idUsuario,
                "ENTREGA",
                "Se registró el préstamo "
                        + guardado.getIdPrestamo());

        return guardado;
    }

    // =========================================
    // REGISTRAR DEVOLUCIÓN
    // =========================================

    @Transactional
    public Prestamo registrarDevolucion(
            Long idPrestamo,
            RegistrarDevolucionRequest datos) {

        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El préstamo no existe."));

        if (!"ACTIVO".equalsIgnoreCase(prestamo.getEstado())) {

            throw new IllegalStateException(
                    "El préstamo ya fue devuelto o no está activo.");
        }

        if (datos.fechaDevolucion()
                .isBefore(prestamo.getFechaEntrega())) {

            throw new IllegalArgumentException(
                    "La devolución no puede ser anterior a la entrega.");
        }

        if (datos.fechaDevolucion().isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "La fecha de devolución no puede estar en el futuro.");
        }

        String condicion = datos.estadoEquipo()
                .trim()
                .toUpperCase();

        if (!List.of(
                "BUENO",
                "DAÑADO",
                "MANTENIMIENTO",
                "PERDIDO"
        ).contains(condicion)) {

            throw new IllegalArgumentException(
                    "Estado inválido. Use BUENO, DAÑADO, MANTENIMIENTO o PERDIDO.");
        }

        prestamo.setFechaDevolucionReal(
                datos.fechaDevolucion());

        prestamo.setEstado("DEVUELTO");

        if (datos.observaciones() != null
                && !datos.observaciones().isBlank()) {

            prestamo.setObservaciones(datos.observaciones());
        }

        Equipo equipo = prestamo.getEquipo();

        if ("BUENO".equals(condicion)) {

            equipo.setEstado("DISPONIBLE");

        } else if ("PERDIDO".equals(condicion)) {

            equipo.setEstado("INACTIVO");

        } else {

            equipo.setEstado("MANTENIMIENTO");
        }

        equipoRepository.save(equipo);

        Prestamo actualizado = prestamoRepository.save(prestamo);

        registrarAuditoria(
                prestamo.getIdUsuario(),
                "DEVOLUCION",
                "Se registró la devolución del préstamo "
                        + idPrestamo
                        + ". Estado recibido: "
                        + condicion);

        // Registrar novedad cuando el equipo no está en buen estado
        if (!"BUENO".equals(condicion)) {

            Novedad novedad = new Novedad();

            novedad.setIdPrestamo(idPrestamo);
            novedad.setTipoNovedad(condicion);

            if (datos.observaciones() == null
                    || datos.observaciones().isBlank()) {

                novedad.setDescripcion(
                        "Equipo recibido en estado " + condicion);

            } else {

                novedad.setDescripcion(
                        datos.observaciones());
            }

            novedad.setEstadoEquipo(condicion);

            novedadRepository.save(novedad);
        }

        return actualizado;
    }

    // =========================================
    // REGISTRAR NOVEDAD
    // =========================================

    @Transactional
    public Novedad registrarNovedad(
            RegistrarNovedadRequest datos) {

        Prestamo prestamo = prestamoRepository
                .findById(datos.idPrestamo())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "El préstamo no existe."));

        Novedad novedad = new Novedad();

        novedad.setIdPrestamo(prestamo.getIdPrestamo());
        novedad.setTipoNovedad(
                datos.tipoNovedad().trim().toUpperCase());
        novedad.setDescripcion(
                datos.descripcion().trim());
        novedad.setEstadoEquipo(datos.estadoEquipo());

        Novedad guardada = novedadRepository.save(novedad);

        registrarAuditoria(
                prestamo.getIdUsuario(),
                "NOVEDAD",
                "Se registró una novedad para el préstamo "
                        + prestamo.getIdPrestamo());

        if (datos.estadoEquipo() != null) {

            String estado = datos.estadoEquipo()
                    .trim()
                    .toUpperCase();

            if (List.of(
                    "DAÑADO",
                    "MANTENIMIENTO",
                    "PERDIDO"
            ).contains(estado)) {

                Equipo equipo = prestamo.getEquipo();

                if ("PERDIDO".equals(estado)) {

                    equipo.setEstado("INACTIVO");

                } else {

                    equipo.setEstado("MANTENIMIENTO");
                }

                equipoRepository.save(equipo);
            }
        }

        return guardada;
    }

    // =========================================
    // LISTAR PRÉSTAMOS ACTIVOS
    // =========================================

    public List<Prestamo> listarActivos() {

        return prestamoRepository
                .findByEstadoIgnoreCase("ACTIVO");
    }

    // =========================================
    // LISTAR HISTORIAL
    // =========================================

    public List<Prestamo> listarHistorial() {

        return prestamoRepository.findAll();
    }

    // =========================================
    // LISTAR PRÉSTAMOS VENCIDOS
    // =========================================

    public List<Prestamo> listarVencidos() {

        return prestamoRepository
                .findByEstadoIgnoreCaseAndFechaDevolucionPrevistaBefore(
                        "ACTIVO",
                        LocalDate.now());
    }

    // =========================================
    // LISTAR PRÉSTAMOS POR USUARIO
    // =========================================

    public List<Prestamo> listarPorUsuario(Long idUsuario) {

        return prestamoRepository
                .findByIdUsuarioOrderByFechaEntregaDesc(idUsuario);
    }

    // =========================================
    // LISTAR NOVEDADES DE UN PRÉSTAMO
    // =========================================

    public List<Novedad> listarNovedades(Long idPrestamo) {

        return novedadRepository
                .findByIdPrestamoOrderByFechaNovedadDesc(idPrestamo);
    }

    // =========================================
    // LISTAR AUDITORÍA
    // =========================================

    public List<Auditoria> listarAuditoria() {

        return auditoriaRepository
                .findAllByOrderByFechaDesc();
    }

    // =========================================
    // GENERAR REPORTE
    // =========================================

    public Map<String, Long> obtenerReporte() {

        Map<String, Long> reporte = new LinkedHashMap<>();

        reporte.put(
                "totalPrestamos",
                prestamoRepository.count());

        reporte.put(
                "prestamosActivos",
                (long) listarActivos().size());

        reporte.put(
                "prestamosVencidos",
                (long) listarVencidos().size());

        reporte.put(
                "prestamosDevueltos",
                (long) prestamoRepository
                        .findByEstadoIgnoreCase("DEVUELTO").size());

        reporte.put(
                "totalNovedades",
                novedadRepository.count());

        return reporte;
    }

    // =========================================
    // REGISTRAR AUDITORÍA
    // =========================================

    private void registrarAuditoria(
            Long idUsuario,
            String accion,
            String descripcion) {

        Auditoria auditoria = new Auditoria();

        auditoria.setIdUsuario(idUsuario);
        auditoria.setAccion(accion);
        auditoria.setDescripcion(descripcion);

        auditoriaRepository.save(auditoria);
    }
}
