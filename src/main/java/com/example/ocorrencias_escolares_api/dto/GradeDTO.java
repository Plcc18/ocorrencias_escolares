package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.GradeShift;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GradeDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID da turma (gerado automaticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Schema(description = "Nome da turma/série", example = "3º DS A")
    private String name;

    @NotNull(message = "Course ID is mandatory")
    @Schema(description = "ID do curso", example = "1")
    private Long courseId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Nome do curso (retornado na leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private String courseName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Sigla do curso (retornado na leitura)", accessMode = Schema.AccessMode.READ_ONLY)
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