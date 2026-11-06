	package com.uniajc.sistemaprestamos.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniajc.sistemaprestamos.entity.Prestamo;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    // Comprobar si una solicitud ya tiene un préstamo
    boolean existsByIdSolicitud(Long idSolicitud);

    // Buscar préstamos por estado
    List<Prestamo> findByEstadoIgnoreCase(String estado);

    // Buscar préstamos vencidos
    List<Prestamo> findByEstadoIgnoreCaseAndFechaDevolucionPrevistaBefore(
            String estado, LocalDate fecha);

    // Consultar préstamos de un usuario
    List<Prestamo> findByIdUsuarioOrderByFechaEntregaDesc(Long idUsuario);
}