package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
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
    @Operation(summary = "Listar alunos")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<StudentDTO>> findAll() {
        List<StudentDTO> list = service.findAll()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover aluno")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
