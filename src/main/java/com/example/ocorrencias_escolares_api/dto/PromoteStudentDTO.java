package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory.EnrollmentReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PromoteStudentDTO {

    @NotNull(message = "Target grade ID is mandatory")
    @Schema(description = "ID da turma destino", example = "5")
    private Long targetGradeId;

    @Schema(description = "Motivo da movimentação", example = "PROMOCAO",
            allowableValues = {"PROMOCAO", "RETENCAO", "TRANSFERENCIA"})
    private EnrollmentReason reason = EnrollmentReason.PROMOCAO;

    @Schema(description = "Observações opcionais", example = "Aprovado com média 8.5")
    private String notes;
}