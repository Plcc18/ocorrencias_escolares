package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.GradeShift;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID da turma (gerado automaticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "Grade level is mandatory")
    @Min(value = 1, message = "Grade level must be between 1 and 3")
    @Max(value = 3, message = "Grade level must be between 1 and 3")
    @Schema(description = "Série da turma (1, 2 ou 3)", example = "3")
    private Integer gradeLevel;

    @NotNull(message = "School year is mandatory")
    @Min(value = 2000)
    @Max(value = 2100)
    @Schema(description = "Ano letivo", example = "2025")
    private Integer schoolYear;

    @NotNull(message = "Course ID is mandatory")
    @Schema(description = "ID do curso", example = "1")
    private Long courseId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Nome visual gerado (ex: '3º DS - 2025')", accessMode = Schema.AccessMode.READ_ONLY)
    private String displayName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Nome do curso", accessMode = Schema.AccessMode.READ_ONLY)
    private String courseName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Sigla do curso", accessMode = Schema.AccessMode.READ_ONLY)
    private String courseAcronym;

    @NotNull(message = "Shift is mandatory")
    @Schema(description = "Turno da turma", example = "MANHA",
            allowableValues = {"MANHA", "TARDE", "NOITE", "INTEGRAL"})
    private GradeShift shift;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Quantidade de alunos na turma", accessMode = Schema.AccessMode.READ_ONLY)
    private Long studentCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}