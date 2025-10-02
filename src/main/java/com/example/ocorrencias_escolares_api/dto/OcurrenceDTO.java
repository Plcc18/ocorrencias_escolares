package com.example.ocorrencias_escolares_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OcurrenceDTO {

    private Long Id;

    @NotBlank
    private String description;

    @NotNull
    private LocalDate ocurranceDate;

    @NotNull
    private Long studentId;

    @NotNull
    private Long teacherId;

    @NotBlank
    private String ocurrenceType;
}
