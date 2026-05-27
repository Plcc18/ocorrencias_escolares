package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.GradeShift;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "ID do aluno (gerado automaticamente)", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 100)
    @Schema(description = "Nome completo do aluno", example = "Pedro Lucas")
    private String name;

    @Email(message = "Email must be valid")
    @Size(max = 100)
    @Schema(description = "Email do aluno", example = "pedro@example.com")
    private String email;

    @NotBlank(message = "Enrollment is mandatory")
    @Size(max = 30)
    @Schema(description = "Matrícula do aluno", example = "2024001")
    private String enrollment;

    @NotNull(message = "Grade ID is mandatory")
    @Schema(description = "ID da turma", example = "1")
    private Long gradeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Nome da turma (retornado na leitura)", accessMode = Schema.AccessMode.READ_ONLY)
    private String gradeName;

    @NotBlank(message = "Course is mandatory")
    @Size(max = 100)
    @Schema(description = "Curso do aluno", example = "Ensino Médio")
    private String course;

    @NotNull(message = "Shift is mandatory")
    @Schema(description = "Turno", example = "MANHA",
            allowableValues = {"MANHA", "TARDE", "NOITE", "INTEGRAL"})
    private GradeShift shift;

    @Schema(description = "Status do aluno", example = "ATIVO",
            allowableValues = {"ATIVO", "INATIVO"})
    private String status = "ATIVO";

    @Schema(description = "Data de nascimento", example = "2008-05-10")
    private String birthDate;

    @Size(max = 100)
    @Schema(description = "Nome do responsável")
    private String guardian;

    @Size(max = 20)
    @Schema(description = "Telefone do responsável")
    private String guardianPhone;

    @Email
    @Size(max = 100)
    @Schema(description = "Email do responsável")
    private String guardianEmail;

    @Schema(description = "Observações adicionais")
    private String notes;
}