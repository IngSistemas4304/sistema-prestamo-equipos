
package com.uniajc.sistemaprestamos.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniajc.sistemaprestamos.entity.Solicitud;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByIdUsuario(Integer idUsuario);

    List<Solicitud> findByEstado(String estado);

    List<Solicitud> findByEstadoIgnoreCase(String estado);

    List<Solicitud> findByIdUsuarioAndEstado(
            Integer idUsuario, String estado);

    boolean existsByIdEquipoAndEstadoInAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long idEquipo,
            List<String> estados,
            LocalDate fechaFin,
            LocalDate fechaInicio
    );
}
