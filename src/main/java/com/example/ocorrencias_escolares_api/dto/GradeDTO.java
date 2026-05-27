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
    @Schema(description = "ID da turma (gerado automaticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Schema(description = "Nome da turma/série", example = "1º A")
    private String name;

    @NotBlank(message = "Course is mandatory")
    @Size(max = 100)
    @Schema(description = "Curso da turma", example = "Ensino Médio")
    private String course;

    @NotNull(message = "Shift is mandatory")
    @Schema(description = "Turno da turma", example = "MANHA",
            allowableValues = {"MANHA", "TARDE", "NOITE", "INTEGRAL"})
    private GradeShift shift;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Quantidade de alunos na turma", accessMode = Schema.AccessMode.READ_ONLY)
    private Long studentCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Data de criação", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Data de atualização", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}