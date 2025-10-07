package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.TeacherRepository;
import com.example.ocorrencias_escolares_api.service.TeacherService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository repository;
    private final ModelMapper modelMapper;

    public TeacherServiceImpl(TeacherRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Teacher create(TeacherDTO dto) {
        Teacher teacher = modelMapper.map(dto, Teacher.class);
        return repository.save(teacher);
    }

    @Override
    public Teacher update(Long id, TeacherDTO dto) {
        Teacher teacher = findById(id);
        modelMapper.map(dto, teacher);
        return repository.save(teacher);
    }

    @Override
    public Teacher findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Teacher not found")) ;
    }

    @Override
    public List<Teacher> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
