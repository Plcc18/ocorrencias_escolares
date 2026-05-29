package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.service.StudentService;
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

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Alunos", description = "Gerenciamento de alunos")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Cadastrar novo aluno")
    public ResponseEntity<StudentDTO> create(@Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(service.create(dto)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Atualizar dados de um aluno")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        return ResponseEntity.ok(toDTO(service.update(id, dto)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Buscar aluno por ID")
    public ResponseEntity<StudentDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(service.findById(id)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar alunos (paginado, com filtros opcionais)")
    public ResponseEntity<Page<StudentDTO>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long gradeId,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 15, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(service.findAll(name, gradeId, status, pageable).map(this::toDTO));
    }

    @GetMapping("/grade/{gradeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar alunos de uma turma específica")
    public ResponseEntity<List<StudentDTO>> findByGrade(@PathVariable Long gradeId) {
        return ResponseEntity.ok(
                service.findByGradeId(gradeId).stream().map(this::toDTO).toList()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover aluno")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private StudentDTO toDTO(Student s) {
        StudentDTO dto = new StudentDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setEmail(s.getEmail());
        dto.setEnrollment(s.getEnrollment());
        dto.setGradeId(s.getGrade().getId());
        dto.setGradeName(s.getGrade().getName());
        dto.setCourseName(s.getGrade().getCourse().getName());
        dto.setCourseAcronym(s.getGrade().getCourse().getAcronym());
        dto.setShift(s.getShift());
        dto.setStatus(s.getStatus());
        dto.setBirthDate(s.getBirthDate());
        dto.setGuardian(s.getGuardian());
        dto.setGuardianPhone(s.getGuardianPhone());
        dto.setGuardianEmail(s.getGuardianEmail());
        dto.setNotes(s.getNotes());
        return dto;
    }
}