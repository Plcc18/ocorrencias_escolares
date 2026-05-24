package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.repository.StudentRepository;
import com.example.ocorrencias_escolares_api.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final OccurrenceRepository occurrenceRepository;
    private final ModelMapper modelMapper;

    public StudentServiceImpl(StudentRepository repository,
                              OccurrenceRepository occurrenceRepository,
                              ModelMapper modelMapper) {
        this.repository = repository;
        this.occurrenceRepository = occurrenceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Student create(StudentDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado para outro aluno: " + dto.getEmail());
        }
        Student student = modelMapper.map(dto, Student.class);
        student.setId(null);
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
        student.setEmail(dto.getEmail());
        student.setName(dto.getName());
        student.setGrade(dto.getGrade());
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
    public Page<Student> findAll(Pageable pageable) {
        return repository.findAll(pageable);
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