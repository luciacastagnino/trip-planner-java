package com.example.demo.DTOs.Activity.Request;

import com.example.demo.enums.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyActivityCreateDTO {

    @Schema(description = "Precio de la actividad (no puede ser negativo)", example = "1500.50")
    @NotNull(message = "El precio es obligatorio.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El precio no puede ser negativo.")
    private Double price;

    @Schema(description = "Nombre de la actividad", example = "Tour por el centro histórico")
    @NotBlank(message = "El nombre es obligatorio.")
    private String name;

    @Schema(description = "Descripción de la actividad", example = "Recorrido guiado por los principales puntos históricos de la ciudad.")
    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @Schema(description = "Categoría de la actividad", example = "AVENTURA")
    @NotNull(message = "La categoría es obligatoria.")
    private ActivityCategory category;

    @Schema(description = "Fecha en la que se realizará la actividad (hoy o en el futuro)", example = "2025-06-10")
    @NotNull(message = "La fecha es obligatoria.")
    @FutureOrPresent(message = "La fecha debe ser hoy o en el futuro.")
    private LocalDate date;

    @Schema(description = "Hora de inicio de la actividad", example = "09:00")
    @NotNull(message = "La hora de inicio es obligatoria.")
    private LocalTime startTime;

    @Schema(description = "Hora de finalización de la actividad", example = "12:00")
    @NotNull(message = "La hora de fin es obligatoria.")
    private LocalTime endTime;

    @Schema(description = "ID de la empresa que ofrece la actividad", example = "3")
    private Long companyId;

    @Schema(description = "Disponibilidad de personas en la actividad", example = "12:00")
    @NotNull(message = "La cantidad de lugares disponibles es obligatoria.")
    private Long available_quantity;

}

