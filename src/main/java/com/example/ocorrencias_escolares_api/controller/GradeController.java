package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.GradeDTO;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.service.GradeService;
import com.example.ocorrencias_escolares_api.service.impl.GradeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@Tag(name = "Turmas", description = "Gerenciamento de turmas")
public class GradeController {

    private final GradeService service;

    public GradeController(GradeService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar nova turma")
    public ResponseEntity<GradeDTO> create(@Valid @RequestBody GradeDTO dto) {
        Grade grade = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(grade));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar turma")
    public ResponseEntity<GradeDTO> update(@PathVariable Long id, @Valid @RequestBody GradeDTO dto) {
        Grade grade = service.update(id, dto);
        return ResponseEntity.ok(toDTO(grade));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Buscar turma por ID")
    public ResponseEntity<GradeDTO> findById(@PathVariable Long id) {
        Grade grade = service.findById(id);
        return ResponseEntity.ok(toDTO(grade));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar todas as turmas com contagem de alunos")
    public ResponseEntity<List<GradeDTO>> findAll() {
        return ResponseEntity.ok(service.findAllWithStudentCount());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover turma")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private GradeDTO toDTO(Grade grade) {
        return ((GradeServiceImpl) service).toDTO(grade, service.countStudents(grade.getId()));
    }
}