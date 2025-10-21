package com.example.ocorrencias_escolares_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OccurrenceDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID da ocorrência (gerado automaticamente pelo sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank
    @Schema(description = "Descrição detalhada da ocorrência", example = "Aluno chegou atrasado")
    private String description;

    @NotNull
    @Schema(description = "Data em que a ocorrência aconteceu", example = "2025-10-21")
    private LocalDate occurrenceDate;

    @NotNull
    @Schema(description = "ID do aluno relacionado à ocorrência", example = "10")
    private Long studentId;

    @NotNull
    @Schema(description = "ID do professor que registrou a ocorrência", example = "5")
    private Long teacherId;

    @NotBlank
    @Schema(description = "Tipo da ocorrência", example = "DISCIPLINA")
    private String occurrenceType;
}
