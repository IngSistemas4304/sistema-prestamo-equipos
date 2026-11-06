package com.uniajc.sistemaprestamos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniajc.sistemaprestamos.entity.Auditoria;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

   
    List<Auditoria> findAllByOrderByFechaDesc();
}