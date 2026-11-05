package com.uniajc.sistemaprestamos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.uniajc.sistemaprestamos.entity.Equipo;

public interface EquipoRepository extends JpaRepository<Equipo, Long> {

    List<Equipo> findByCategoria_IdCategoria(Long idCategoria);

    List<Equipo> findByEstadoIgnoreCase(String estado);

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdEquipoNot(String codigo, Long idEquipo);

    List<Equipo> findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String codigo, String nombre, String descripcion);
}