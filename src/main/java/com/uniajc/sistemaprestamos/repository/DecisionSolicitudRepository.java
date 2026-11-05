package com.uniajc.sistemaprestamos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniajc.sistemaprestamos.entity.DecisionSolicitud;

public interface DecisionSolicitudRepository
        extends JpaRepository<DecisionSolicitud, Long> {

    List<DecisionSolicitud> findByIdSolicitud(Long idSolicitud);

    List<DecisionSolicitud> findByIdResponsable(Long idResponsable);
}
