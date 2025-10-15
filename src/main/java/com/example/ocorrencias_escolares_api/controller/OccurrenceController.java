package com.example.ocorrencias_escolares_api.controller;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.service.OccurrenceService;
import com.example.ocorrencias_escolares_api.service.TeacherService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/occurrences")
public class OccurrenceController {

    private final OccurrenceService service;
    private final ModelMapper modelMapper;

    public OccurrenceController(OccurrenceService service, ModelMapper modelMapper) {
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<OccurrenceDTO> create(@Valid @RequestBody OccurrenceDTO dto) {
        Occurrence occurrence = service.create(dto);
        return new ResponseEntity<>(modelMapper.map(occurrence, OccurrenceDTO.class), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<OccurrenceDTO> update(@PathVariable Long id, @Valid @RequestBody OccurrenceDTO dto) {
        Occurrence occurrence = service.update(id, dto);
        return ResponseEntity.ok(modelMapper.map(occurrence, OccurrenceDTO.class));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    public ResponseEntity<OccurrenceDTO> findById(@PathVariable Long id) {
        Occurrence occurrence = service.findById(id);
        return ResponseEntity.ok(modelMapper.map(occurrence, OccurrenceDTO.class));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<OccurrenceDTO>> findAll() {
        List<OccurrenceDTO> dtos = service.findAll().stream()
                .map(occurrence -> modelMapper.map(occurrence, OccurrenceDTO.class))
                .collect(Collectors .toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
