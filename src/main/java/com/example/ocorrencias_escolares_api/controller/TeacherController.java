package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.dto.ChangePasswordDTO;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.service.TeacherService;
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
@RequestMapping("/api/teachers")
@Tag(name = "Professores", description = "Gerenciamento de professores")
public class TeacherController {

    private final TeacherService service;
    private final ModelMapper modelMapper;

    public TeacherController(TeacherService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar novo professor")
    public ResponseEntity<TeacherDTO> create(@Valid @RequestBody TeacherDTO dto) {
        Teacher teacher = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(teacher, TeacherDTO.class));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar dados de um professor (nome, email, disciplina)")
    public ResponseEntity<TeacherDTO> update(@PathVariable Long id, @Valid @RequestBody TeacherDTO dto) {
        Teacher teacher = service.update(id, dto);
        return ResponseEntity.ok(modelMapper.map(teacher, TeacherDTO.class));
    }

    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alterar senha de um professor (somente ADMIN)")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDTO dto) {
        service.changePassword(id, dto.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Buscar professor por ID")
    public ResponseEntity<TeacherDTO> findById(@PathVariable Long id) {
        Teacher teacher = service.findById(id);
        return ResponseEntity.ok(modelMapper.map(teacher, TeacherDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Listar todos os professores")
    public ResponseEntity<List<TeacherDTO>> findAll() {
        List<TeacherDTO> list = service.findAll()
                .stream()
                .map(s -> modelMapper.map(s, TeacherDTO.class))
                .toList();
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remover professor")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}