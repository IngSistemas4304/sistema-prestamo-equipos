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
    private final EquipoRepository equipoRepository;
    private final NovedadRepository novedadRepository;
    private final AuditoriaRepository auditoriaRepository;

    public PrestamoService(
            PrestamoRepository prestamoRepository,
            EquipoRepository equipoRepository,
            NovedadRepository novedadRepository,
            AuditoriaRepository auditoriaRepository) {

        this.prestamoRepository = prestamoRepository;
        this.equipoRepository = equipoRepository;
        this.novedadRepository = novedadRepository;
        this.auditoriaRepository = auditoriaRepository;
    }


    @Transactional
    public Prestamo registrarEntrega(RegistrarPrestamoRequest datos) {

        if (datos.fechaDevolucionPrevista().isBefore(datos.fechaEntrega())) {
            throw new IllegalArgumentException(
                    "La fecha prevista no puede ser anterior a la fecha de entrega.");
        }

        if (datos.fechaEntrega().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "La fecha de entrega no puede estar en el futuro.");
        }

        if (prestamoRepository.existsByIdSolicitud(datos.idSolicitud())) {
            throw new IllegalStateException(
                    "Esta solicitud ya tiene un préstamo registrado.");
        }

        Equipo equipo = equipoRepository.findById(datos.idEquipo())
                .orElseThrow(() ->
                        new IllegalArgumentException("El equipo indicado no existe."));

        if (!"DISPONIBLE".equalsIgnoreCase(equipo.getEstado())) {
            throw new IllegalStateException(
                    "El equipo no está disponible para préstamo.");
        }

        Prestamo prestamo = new Prestamo();

        prestamo.setIdSolicitud(datos.idSolicitud());
        prestamo.setIdUsuario(datos.idUsuario());
        prestamo.setEquipo(equipo);
        prestamo.setFechaEntrega(datos.fechaEntrega());
        prestamo.setFechaDevolucionPrevista(datos.fechaDevolucionPrevista());
        prestamo.setObservaciones(datos.observaciones());
        prestamo.setEstado("ACTIVO");

        // Cambiar el estado del equipo
        equipo.setEstado("PRESTADO");

        equipoRepository.save(equipo);

        Prestamo guardado = prestamoRepository.save(prestamo);

        registrarAuditoria(
                datos.idUsuario(),
                "ENTREGA",
                "Se registró el préstamo " + guardado.getIdPrestamo());

        return guardado;
    }


    @Transactional
    public Prestamo registrarDevolucion(
            Long idPrestamo,
            RegistrarDevolucionRequest datos) {

        Prestamo prestamo = prestamoRepository.findById(idPrestamo)
                .orElseThrow(() ->
                        new IllegalArgumentException("El préstamo no existe."));

        if (!"ACTIVO".equalsIgnoreCase(prestamo.getEstado())) {
            throw new IllegalStateException(
                    "El préstamo ya fue devuelto o no está activo.");
        }

        if (datos.fechaDevolucion().isBefore(prestamo.getFechaEntrega())) {
            throw new IllegalArgumentException(
                    "La devolución no puede ser anterior a la entrega.");
        }

        String condicion = datos.estadoEquipo().trim().toUpperCase();

        if (!List.of("BUENO", "DAÑADO", "MANTENIMIENTO", "PERDIDO")
                .contains(condicion)) {

            throw new IllegalArgumentException(
                    "Estado inválido. Use BUENO, DAÑADO, MANTENIMIENTO o PERDIDO.");
        }

        prestamo.setFechaDevolucionReal(datos.fechaDevolucion());
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
                "Se registró la devolución del préstamo " + idPrestamo
                        + ". Estado recibido: " + condicion);


        if (!"BUENO".equals(condicion)) {

            Novedad novedad = new Novedad();

            novedad.setIdPrestamo(idPrestamo);
            novedad.setTipoNovedad(condicion);

            if (datos.observaciones() == null
                    || datos.observaciones().isBlank()) {

                novedad.setDescripcion(
                        "Equipo recibido en estado " + condicion);

            } else {
                novedad.setDescripcion(datos.observaciones());
            }

            novedad.setEstadoEquipo(condicion);

            novedadRepository.save(novedad);
        }

        return actualizado;
    }



    @Transactional
    public Novedad registrarNovedad(RegistrarNovedadRequest datos) {

        Prestamo prestamo = prestamoRepository.findById(datos.idPrestamo())
                .orElseThrow(() ->
                        new IllegalArgumentException("El préstamo no existe."));

        Novedad novedad = new Novedad();

        novedad.setIdPrestamo(prestamo.getIdPrestamo());
        novedad.setTipoNovedad(datos.tipoNovedad().trim().toUpperCase());
        novedad.setDescripcion(datos.descripcion().trim());
        novedad.setEstadoEquipo(datos.estadoEquipo());

        Novedad guardada = novedadRepository.save(novedad);

        registrarAuditoria(
                prestamo.getIdUsuario(),
                "NOVEDAD",
                "Se registró una novedad para el préstamo "
                        + prestamo.getIdPrestamo());

        if (datos.estadoEquipo() != null) {

            String estado = datos.estadoEquipo().trim().toUpperCase();

            if (List.of("DAÑADO", "MANTENIMIENTO", "PERDIDO")
                    .contains(estado)) {

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

  

    public List<Prestamo> listarActivos() {

        return prestamoRepository.findByEstadoIgnoreCase("ACTIVO");
    }



    public List<Prestamo> listarHistorial() {

        return prestamoRepository.findAll();
    }



    public List<Prestamo> listarVencidos() {

        return prestamoRepository
                .findByEstadoIgnoreCaseAndFechaDevolucionPrevistaBefore(
                        "ACTIVO", LocalDate.now());
    }

  

    public List<Prestamo> listarPorUsuario(Long idUsuario) {

        return prestamoRepository
                .findByIdUsuarioOrderByFechaEntregaDesc(idUsuario);
    }



    public List<Novedad> listarNovedades(Long idPrestamo) {

        return novedadRepository
                .findByIdPrestamoOrderByFechaNovedadDesc(idPrestamo);
    }


    public List<Auditoria> listarAuditoria() {

        return auditoriaRepository.findAllByOrderByFechaDesc();
    }



    public Map<String, Long> obtenerReporte() {

        Map<String, Long> reporte = new LinkedHashMap<>();

        reporte.put("totalPrestamos", prestamoRepository.count());

        reporte.put("prestamosActivos",
                (long) listarActivos().size());

        reporte.put("prestamosVencidos",
                (long) listarVencidos().size());

        reporte.put("prestamosDevueltos",
                (long) prestamoRepository
                        .findByEstadoIgnoreCase("DEVUELTO").size());

        reporte.put("totalNovedades", novedadRepository.count());

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