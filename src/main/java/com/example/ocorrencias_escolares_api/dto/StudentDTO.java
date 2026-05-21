package com.example.ocorrencias_escolares_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID do aluno (gerado automaticamente)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email must be valid")
    @Size(max = 100)
    @Schema(description = "Email do aluno", example = "pedro@example.com")
    private String email;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100)
    @Schema(description = "Nome completo do aluno", example = "Pedro Lucas")
    private String name;

    @NotBlank(message = "Grade is mandatory")
    @Size(max = 20)
    @Schema(description = "Série/ano do aluno", example = "1º Ano")
    private String grade;
}
