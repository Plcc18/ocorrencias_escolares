package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.dto.OccurrenceFilterDTO;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.service.OccurrenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/occurrences")
@Tag(name = "Ocorrências", description = "Gerenciamento de ocorrências escolares")
public class OccurrenceController {

    private final OccurrenceService service;

    public OccurrenceController(OccurrenceService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Registrar nova ocorrência")
    public ResponseEntity<OccurrenceDTO> create(@Valid @RequestBody OccurrenceDTO dto) {
        Occurrence occurrence = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(occurrence));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Atualizar ocorrência")
    public ResponseEntity<OccurrenceDTO> update(@PathVariable Long id, @Valid @RequestBody OccurrenceDTO dto) {
        Occurrence occurrence = service.update(id, dto);
        return ResponseEntity.ok(toDTO(occurrence));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Buscar ocorrência por ID")
    public ResponseEntity<OccurrenceDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(service.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar ocorrências com filtros opcionais (paginado)",
            description = "Filtros disponíveis: studentId, teacherId, occurrenceType, startDate, endDate")
    public ResponseEntity<Page<OccurrenceDTO>> findAll(
            OccurrenceFilterDTO filter,
            @PageableDefault(size = 20, sort = "occurrenceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OccurrenceDTO> page = service.findWithFilters(filter, pageable).map(this::toDTO);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Listar ocorrências de um aluno específico")
    public ResponseEntity<Page<OccurrenceDTO>> findByStudent(
            @PathVariable Long studentId,
            @PageableDefault(size = 20, sort = "occurrenceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        OccurrenceFilterDTO filter = new OccurrenceFilterDTO();
        filter.setStudentId(studentId);
        Page<OccurrenceDTO> page = service.findWithFilters(filter, pageable).map(this::toDTO);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar ocorrências registradas por um professor")
    public ResponseEntity<Page<OccurrenceDTO>> findByTeacher(
            @PathVariable Long teacherId,
            @PageableDefault(size = 20, sort = "occurrenceDate", direction = Sort.Direction.DESC) Pageable pageable) {
        OccurrenceFilterDTO filter = new OccurrenceFilterDTO();
        filter.setTeacherId(teacherId);
        Page<OccurrenceDTO> page = service.findWithFilters(filter, pageable).map(this::toDTO);
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover ocorrência")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Manual mapping to avoid ModelMapper conflict with studentId/teacherId
    private OccurrenceDTO toDTO(Occurrence o) {
        OccurrenceDTO dto = new OccurrenceDTO();
        dto.setId(o.getId());
        dto.setDescription(o.getDescription());
        dto.setOccurrenceDate(o.getOccurrenceDate());
        dto.setOccurrenceType(o.getOccurrenceType());
        dto.setStudentId(o.getStudent().getId());
        dto.setStudentName(o.getStudent().getName());
        dto.setTeacherId(o.getTeacher().getId());
        dto.setTeacherName(o.getTeacher().getName());
        return dto;
    }
}
