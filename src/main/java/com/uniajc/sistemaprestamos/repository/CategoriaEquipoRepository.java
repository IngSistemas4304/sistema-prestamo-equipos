package com.uniajc.sistemaprestamos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.uniajc.sistemaprestamos.entity.CategoriaEquipo;

public interface CategoriaEquipoRepository
        extends JpaRepository<CategoriaEquipo, Long> {

}