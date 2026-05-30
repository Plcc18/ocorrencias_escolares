package com.example.ocorrencias_escolares_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

    @NotBlank(message = "Subject is mandatory")
    @Size(max = 50)
    @Schema(description = "Disciplina ministrada", example = "Matemática")
    private String subject;

    /**
     * Senha para login.
     * - Obrigatória na criação (validada no TeacherServiceImpl).
     * - Ignorada na edição (PUT /api/teachers/{id} não altera senha).
     * - Para alterar senha use PATCH /api/teachers/{id}/password.
     *
     * Sem @Size aqui para evitar falha de validação quando o campo chega
     * vazio na edição. A constraint de tamanho mínimo (6) é aplicada
     * programaticamente no service durante a criação.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(
            description = "Senha para login (obrigatória na criação, ignorada na edição). " +
                    "Para alterar senha use PATCH /api/teachers/{id}/password.",
            example = "senha123",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;
}