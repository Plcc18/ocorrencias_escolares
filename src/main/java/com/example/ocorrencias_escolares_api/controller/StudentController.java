package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Alunos", description = "Gerenciamento de alunos")
public class StudentController {

    private final StudentService service;
    private final ModelMapper modelMapper;

    public StudentController(StudentService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Cadastrar novo aluno")
    public ResponseEntity<StudentDTO> create(@Valid @RequestBody StudentDTO dto) {
        Student student = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(student, StudentDTO.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Atualizar dados de um aluno")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        Student student = service.update(id, dto);
        return ResponseEntity.ok(modelMapper.map(student, StudentDTO.class));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Buscar aluno por ID")
    public ResponseEntity<StudentDTO> findById(@PathVariable Long id) {
        Student student = service.findById(id);
        return ResponseEntity.ok(modelMapper.map(student, StudentDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar todos os alunos (paginado)")
    public ResponseEntity<Page<StudentDTO>> findAll(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<StudentDTO> page = service.findAll(pageable)
                .map(student -> modelMapper.map(student, StudentDTO.class));
        return ResponseEntity.ok(page);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover aluno")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
