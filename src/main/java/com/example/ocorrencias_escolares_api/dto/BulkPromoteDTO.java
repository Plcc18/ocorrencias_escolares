package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory.EnrollmentReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkPromoteDTO {

    @NotNull(message = "Source grade ID is mandatory")
    @Schema(description = "ID da turma de origem (ex: 1° DS - 2024)", example = "1")
    private Long sourceGradeId;

    @NotNull(message = "Target grade ID is mandatory")
    @Schema(description = "ID da turma destino (ex: 2° DS - 2025)", example = "4")
    private Long targetGradeId;

    @Schema(description = "IDs específicos de alunos a promover. Se vazio, promove todos os ativos da turma.")
    private List<Long> studentIds;

    @Schema(description = "Motivo da movimentação", example = "PROMOCAO",
            allowableValues = {"PROMOCAO", "RETENCAO", "TRANSFERENCIA"})
    private EnrollmentReason reason = EnrollmentReason.PROMOCAO;

    @Schema(description = "Observações gerais")
    private String notes;
}