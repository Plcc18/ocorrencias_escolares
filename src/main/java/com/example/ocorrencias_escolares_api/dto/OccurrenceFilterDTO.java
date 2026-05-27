package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class OccurrenceFilterDTO {

    @Schema(description = "Filtrar por ID do aluno", example = "1")
    private Long studentId;

    @Schema(description = "Filtrar por ID do professor", example = "2")
    private Long teacherId;

    @Schema(description = "Filtrar por ID da turma", example = "3")
    private Long gradeId;

    @Schema(description = "Filtrar por tipo de ocorrência", example = "DISCIPLINA")
    private OccurrenceType occurrenceType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Data inicial do filtro (inclusiva)", example = "2025-01-01")
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(description = "Data final do filtro (inclusiva)", example = "2025-12-31")
    private LocalDate endDate;
}