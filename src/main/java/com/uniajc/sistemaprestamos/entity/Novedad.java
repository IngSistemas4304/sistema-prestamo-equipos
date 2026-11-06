package com.uniajc.sistemaprestamos.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "NOVEDAD")
public class Novedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_novedad")
    private Long idNovedad;

    @Column(name = "id_prestamo", nullable = false)
    private Long idPrestamo;

    @Column(name = "tipo_novedad", nullable = false, length = 40)
    private String tipoNovedad;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "estado_equipo", length = 30)
    private String estadoEquipo;

    @Column(name = "fecha_novedad", nullable = false)
    private LocalDateTime fechaNovedad = LocalDateTime.now();

    public Novedad() {
    }

    public Long getIdNovedad() {
        return idNovedad;
    }

    public void setIdNovedad(Long idNovedad) {
        this.idNovedad = idNovedad;
    }

    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(Long idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public String getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(String tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstadoEquipo() {
        return estadoEquipo;
    }

    public void setEstadoEquipo(String estadoEquipo) {
        this.estadoEquipo = estadoEquipo;
    }

    public LocalDateTime getFechaNovedad() {
        return fechaNovedad;
    }

    public void setFechaNovedad(LocalDateTime fechaNovedad) {
        this.fechaNovedad = fechaNovedad;
    }
}