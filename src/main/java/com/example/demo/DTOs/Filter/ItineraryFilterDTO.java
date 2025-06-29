package com.example.demo.DTOs.Filter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ParameterObject
public class ItineraryFilterDTO {

    @Schema(description = "Fecha inicial para filtrar (formato yyyy-MM-dd)", example = "2025-06-01")
    private String dateFrom;

    @Schema(description = "Fecha final para filtrar (formato yyyy-MM-dd)", example = "2025-06-30")
    private String dateTo;
 }