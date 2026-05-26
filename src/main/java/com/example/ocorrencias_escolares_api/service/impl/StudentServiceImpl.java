package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.repository.StudentRepository;
import com.example.ocorrencias_escolares_api.service.GradeService;
import com.example.ocorrencias_escolares_api.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final OccurrenceRepository occurrenceRepository;
    private final GradeService gradeService;

    public StudentServiceImpl(StudentRepository repository,
                              OccurrenceRepository occurrenceRepository,
                              GradeService gradeService) {
        this.repository = repository;
        this.occurrenceRepository = occurrenceRepository;
        this.gradeService = gradeService;
    }

    @Override
    @Transactional
    public Student create(StudentDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado para outro aluno: " + dto.getEmail());
        }
        Grade grade = gradeService.findById(dto.getGradeId());

        Student student = new Student();
        student.setEmail(dto.getEmail());
        student.setName(dto.getName());
        student.setGrade(grade);
        return repository.save(student);
    }

    @Override
    @Transactional
    public Student update(Long id, StudentDTO dto) {
        Student student = findById(id);
        repository.findByEmail(dto.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Email já cadastrado para outro aluno: " + dto.getEmail());
                });

        Grade grade = gradeService.findById(dto.getGradeId());
        student.setEmail(dto.getEmail());
        student.setName(dto.getName());
        student.setGrade(grade);
        return repository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno não encontrado com id: " + id);
        }
        if (occurrenceRepository.existsByStudentId(id)) {
            throw new BusinessException(
                    "Não é possível remover o aluno pois existem ocorrências vinculadas a ele. " +
                            "Remova as ocorrências antes de excluir o aluno.");
        }
        repository.deleteById(id);
    }
}