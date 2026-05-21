package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OccurrenceDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID da ocorrência (gerado automaticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Description is mandatory")
    @Schema(description = "Descrição detalhada da ocorrência", example = "Aluno chegou atrasado")
    private String description;

    @NotNull(message = "Date is mandatory")
    @Schema(description = "Data da ocorrência", example = "2025-10-21")
    private LocalDate occurrenceDate;

    @NotNull(message = "Student ID is mandatory")
    @Schema(description = "ID do aluno", example = "10")
    private Long studentId;

    @NotNull(message = "Teacher ID is mandatory")
    @Schema(description = "ID do professor que registrou", example = "5")
    private Long teacherId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Nome do aluno (retornado na leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private String studentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Nome do professor (retornado na leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private String teacherName;

    @NotNull(message = "Occurrence type is mandatory")
    @Schema(description = "Tipo da ocorrência", example = "DISCIPLINA",
            allowableValues = {"DISCIPLINA", "FALTA", "ELOGIO", "ADVERTENCIA", "SUSPENSAO", "OUTRO"})
    private OccurrenceType occurrenceType;
}
