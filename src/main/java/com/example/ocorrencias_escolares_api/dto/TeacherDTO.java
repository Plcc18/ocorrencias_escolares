package com.example.ocorrencias_escolares_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeacherDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "ID do professor (gerado automaticamente pelo sistema)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Size(max = 100)
    @Schema(
            description = "Email do professor",
            example = "professor@example.com",
            maxLength = 100
    )
    private String email;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Schema(
            description = "Nome completo do professor",
            example = "Maria Oliveira",
            maxLength = 100
    )
    private String name;

    @NotBlank(message = "Subject is mandatory")
    @Size(max = 50)
    @Schema(
            description = "Disciplina que o professor ministra",
            example = "Matemática",
            maxLength = 50
    )
    private String subject;
}
