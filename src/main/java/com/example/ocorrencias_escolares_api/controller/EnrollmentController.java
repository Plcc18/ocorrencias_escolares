package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.*;
import com.example.ocorrencias_escolares_api.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Matrículas", description = "Histórico de matrículas e promoção de alunos")
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(
            summary = "Histórico completo de turmas de um aluno",
            description = "Retorna todas as turmas pelas quais o aluno passou, do mais recente ao mais antigo."
    )
    public ResponseEntity<List<EnrollmentHistoryDTO>> getHistory(@PathVariable Long studentId) {
        return ResponseEntity.ok(service.getHistory(studentId));
    }

    @PostMapping("/student/{studentId}/promote")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Promover aluno individualmente",
            description = "Move um aluno para uma nova turma. Encerra a matrícula atual e abre uma nova."
    )
    public ResponseEntity<EnrollmentHistoryDTO> promote(
            @PathVariable Long studentId,
            @Valid @RequestBody PromoteStudentDTO dto) {
        return ResponseEntity.ok(service.promoteStudent(studentId, dto));
    }

    @PostMapping("/bulk-promote")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Promoção em lote (virada de ano letivo)",
            description = """
            Move todos os alunos ativos de uma turma para a turma destino.
            
            Fluxo recomendado para virada de ano:
            1. Crie as novas turmas do próximo ano (ex: 2° DS - 2025, 3° DS - 2025)
            2. Use este endpoint para mover os alunos da 1° DS - 2024 → 2° DS - 2025
            3. Alunos retidos: use o motivo RETENCAO ou mova individualmente
            
            O histórico de ocorrências é preservado automaticamente, pois cada
            ocorrência já tem o grade_id snapshot gravado no momento do registro.
            """
    )
    public ResponseEntity<PromotionResultDTO> bulkPromote(@Valid @RequestBody BulkPromoteDTO dto) {
        return ResponseEntity.ok(service.bulkPromote(dto));
    }
}