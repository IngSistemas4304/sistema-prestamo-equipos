package com.uniajc.sistemaprestamos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniajc.sistemaprestamos.entity.Novedad;

public interface NovedadRepository extends JpaRepository<Novedad, Long> {

    // Consultar novedades de un préstamo
    List<Novedad> findByIdPrestamoOrderByFechaNovedadDesc(Long idPrestamo);
}