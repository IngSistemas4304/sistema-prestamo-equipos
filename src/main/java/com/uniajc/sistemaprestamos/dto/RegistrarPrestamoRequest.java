
package com.uniajc.sistemaprestamos.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RegistrarPrestamoRequest(

        @NotNull
        @Positive
        Long idSolicitud,

        @NotNull
        @Positive
        Long idEquipo,

        @NotNull
        LocalDate fechaEntrega,

        @NotNull
        LocalDate fechaDevolucionPrevista,

        String observaciones

) {}
