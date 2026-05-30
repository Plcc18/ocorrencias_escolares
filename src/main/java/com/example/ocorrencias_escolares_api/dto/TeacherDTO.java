package com.example.ocorrencias_escolares_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class TeacherDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID do professor (gerado automaticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100)
    @Schema(description = "Nome completo do professor", example = "Maria Oliveira")
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Size(max = 100)
    @Schema(description = "Email do professor (usado para login)", example = "professor@example.com")
    private String email;

    @NotEmpty(message = "At least one subject is required")
    @Schema(description = "Disciplinas ministradas", example = "[\"Matemática\", \"Física\"]")
    private List<@NotBlank @Size(max = 50) String> subjects;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "Senha para login (obrigatória na criação, ignorada na edição). " +
                    "Para alterar senha use PATCH /api/teachers/{id}/password.",
            example = "senha123",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;
}