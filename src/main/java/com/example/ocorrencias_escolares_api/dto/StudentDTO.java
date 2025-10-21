package com.example.ocorrencias_escolares_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(
            description = "ID do aluno (gerado automaticamente pelo sistema)",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Schema(
            description = "Nome completo do aluno",
            example = "Pedro Lucas",
            maxLength = 100
    )
    private String name;

    @NotBlank
    @Size(max = 20)
    @Schema(
            description = "Série ou grau do aluno",
            example = "1º Ano",
            maxLength = 20
    )
    private String grade;
}
