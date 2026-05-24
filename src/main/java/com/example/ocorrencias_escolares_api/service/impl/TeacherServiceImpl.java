package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.repository.TeacherRepository;
import com.example.ocorrencias_escolares_api.service.TeacherService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;
    private final OccurrenceRepository occurrenceRepository;
    private final ModelMapper modelMapper;

    public TeacherServiceImpl(TeacherRepository repository,
                              OccurrenceRepository occurrenceRepository,
                              ModelMapper modelMapper) {
        this.repository = repository;
        this.occurrenceRepository = occurrenceRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public Teacher create(TeacherDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado para outro professor: " + dto.getEmail());
        }
        Teacher teacher = modelMapper.map(dto, Teacher.class);
        teacher.setId(null);
        return repository.save(teacher);
    }

    @Override
    @Transactional
    public Teacher update(Long id, TeacherDTO dto) {
        Teacher teacher = findById(id);
        repository.findByEmail(dto.getEmail())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("Email já cadastrado para outro professor: " + dto.getEmail());
                });
        teacher.setEmail(dto.getEmail());
        teacher.setName(dto.getName());
        teacher.setSubject(dto.getSubject());
        return repository.save(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public Teacher findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Professor não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Professor não encontrado com id: " + id);
        }
        if (occurrenceRepository.existsByTeacherId(id)) {
            throw new BusinessException(
                    "Não é possível remover o professor pois existem ocorrências vinculadas a ele. " +
                            "Remova as ocorrências antes de excluir o professor.");
        }
        repository.deleteById(id);
    }
}