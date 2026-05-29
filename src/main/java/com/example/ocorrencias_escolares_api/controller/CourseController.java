package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.CourseDTO;
import com.example.ocorrencias_escolares_api.entity.Course;
import com.example.ocorrencias_escolares_api.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Cursos", description = "Gerenciamento de cursos")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar novo curso")
    public ResponseEntity<CourseDTO> create(@Valid @RequestBody CourseDTO dto) {
        Course course = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(course, 0L));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar curso")
    public ResponseEntity<CourseDTO> update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        Course course = service.update(id, dto);
        return ResponseEntity.ok(toDTO(course, 0L));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Buscar curso por ID")
    public ResponseEntity<CourseDTO> findById(@PathVariable Long id) {
        Course course = service.findById(id);
        return ResponseEntity.ok(toDTO(course, 0L));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar todos os cursos com contagem de turmas")
    public ResponseEntity<List<CourseDTO>> findAll() {
        return ResponseEntity.ok(service.findAllWithGradeCount());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover curso")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private CourseDTO toDTO(Course course, Long gradeCount) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setAcronym(course.getAcronym());
        dto.setGradeCount(gradeCount);
        dto.setCreatedAt(course.getCreatedAt());
        dto.setUpdatedAt(course.getUpdatedAt());
        return dto;
    }
}