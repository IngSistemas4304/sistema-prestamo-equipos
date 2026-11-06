package com.uniajc.sistemaprestamos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrarNovedadRequest(

        @NotNull
        @Positive
        Long idPrestamo,

        @NotBlank
        String tipoNovedad,

        @NotBlank
        String descripcion,

        String estadoEquipo

) {
}