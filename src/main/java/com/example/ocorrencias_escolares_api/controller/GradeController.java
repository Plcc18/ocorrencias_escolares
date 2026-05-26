package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.GradeDTO;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@Tag(name = "Turmas", description = "Gerenciamento de turmas e séries")
public class GradeController {

    private final GradeService service;
    private final ModelMapper modelMapper;

    public GradeController(GradeService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar nova turma/série")
    public ResponseEntity<GradeDTO> create(@Valid @RequestBody GradeDTO dto) {
        Grade grade = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(grade));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar turma/série")
    public ResponseEntity<GradeDTO> update(@PathVariable Long id, @Valid @RequestBody GradeDTO dto) {
        Grade grade = service.update(id, dto);
        return ResponseEntity.ok(toDTO(grade));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Buscar turma por ID")
    public ResponseEntity<GradeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(service.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar todas as turmas")
    public ResponseEntity<List<GradeDTO>> findAll() {
        List<GradeDTO> list = service.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover turma")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private GradeDTO toDTO(Grade grade) {
        GradeDTO dto = new GradeDTO();
        dto.setId(grade.getId());
        dto.setName(grade.getName());
        dto.setCreatedAt(grade.getCreatedAt());
        dto.setUpdatedAt(grade.getUpdatedAt());
        return dto;
    }
}