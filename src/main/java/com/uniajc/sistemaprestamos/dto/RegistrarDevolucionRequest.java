package com.uniajc.sistemaprestamos.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record RegistrarDevolucionRequest(

        @NotNull
        LocalDate fechaDevolucion,

        @NotNull
        String estadoEquipo,

        String observaciones

) {
}