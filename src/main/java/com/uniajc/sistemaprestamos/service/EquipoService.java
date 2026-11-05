package com.uniajc.sistemaprestamos.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.uniajc.sistemaprestamos.entity.CategoriaEquipo;
import com.uniajc.sistemaprestamos.entity.Equipo;
import com.uniajc.sistemaprestamos.repository.CategoriaEquipoRepository;
import com.uniajc.sistemaprestamos.repository.EquipoRepository;

@Service
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final CategoriaEquipoRepository categoriaRepository;

    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            "DISPONIBLE",
            "RESERVADO",
            "PRESTADO",
            "MANTENIMIENTO",
            "INACTIVO"
    );

    public EquipoService(
            EquipoRepository equipoRepository,
            CategoriaEquipoRepository categoriaRepository) {

        this.equipoRepository = equipoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Equipo> listarEquipos() {
        return equipoRepository.findAll();
    }

    public Equipo buscarEquipo(Long id) {
        return equipoRepository.findById(id).orElse(null);
    }

    public Equipo guardarEquipo(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    public List<Equipo> filtrarPorCategoria(Long idCategoria) {
        return equipoRepository.findByCategoria_IdCategoria(idCategoria);
    }

    public List<Equipo> filtrarPorEstado(String estado) {
        return equipoRepository.findByEstadoIgnoreCase(estado);
    }

    public boolean existeCodigo(String codigo) {
        return equipoRepository.existsByCodigo(codigo);
    }

    public boolean existeCodigoEnOtroEquipo(String codigo, Long idEquipo) {
        return equipoRepository.existsByCodigoAndIdEquipoNot(codigo, idEquipo);
    }

    public List<Equipo> buscarPorTexto(String texto) {
        return equipoRepository
                .findByCodigoContainingIgnoreCaseOrNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                        texto, texto, texto);
    }

    public String validarDatos(Equipo equipo) {

        if (equipo.getCodigo() == null || equipo.getCodigo().isBlank()) {
            return "El codigo del equipo es obligatorio.";
        }

        if (equipo.getNombre() == null || equipo.getNombre().isBlank()) {
            return "El nombre del equipo es obligatorio.";
        }

        if (equipo.getDescripcion() == null || equipo.getDescripcion().isBlank()) {
            return "La descripcion del equipo es obligatoria.";
        }

        if (equipo.getCaracteristicas() == null || equipo.getCaracteristicas().isBlank()) {
            return "Las caracteristicas del equipo son obligatorias.";
        }

        if (equipo.getEstado() == null || equipo.getEstado().isBlank()) {
            return "El estado del equipo es obligatorio.";
        }

        if (!ESTADOS_VALIDOS.contains(equipo.getEstado().trim().toUpperCase())) {
            return "El estado no es valido. Estados permitidos: DISPONIBLE, RESERVADO, PRESTADO, MANTENIMIENTO, INACTIVO.";
        }

        if (equipo.getCategoria() == null ||
                equipo.getCategoria().getIdCategoria() == null) {
            return "La categoria del equipo es obligatoria.";
        }

        CategoriaEquipo categoria = categoriaRepository.findById(
                equipo.getCategoria().getIdCategoria()
        ).orElse(null);

        if (categoria == null) {
            return "La categoria seleccionada no existe.";
        }

        if (!"ACTIVO".equalsIgnoreCase(categoria.getEstado())) {
            return "No se puede asignar un equipo a una categoria inactiva.";
        }

        return null;
    }

    public Equipo inactivarEquipo(Long id) {

        Equipo equipo = buscarEquipo(id);

        if (equipo == null) {
            return null;
        }

        if ("PRESTADO".equalsIgnoreCase(equipo.getEstado())) {
            throw new IllegalStateException(
                    "No se puede inactivar un equipo que se encuentra prestado.");
        }

        if ("RESERVADO".equalsIgnoreCase(equipo.getEstado())) {
            throw new IllegalStateException(
                    "No se puede inactivar un equipo que se encuentra reservado.");
        }

        equipo.setEstado("INACTIVO");

        return equipoRepository.save(equipo);
    }

    public boolean puedePrestarse(Long idEquipo) {

        Equipo equipo = buscarEquipo(idEquipo);

        if (equipo == null) {
            return false;
        }

        return "DISPONIBLE".equalsIgnoreCase(equipo.getEstado());
    }
}