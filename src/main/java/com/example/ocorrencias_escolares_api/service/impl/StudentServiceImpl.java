package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.StudentRepository;
import com.example.ocorrencias_escolares_api.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final ModelMapper modelMapper;

    public StudentServiceImpl(StudentRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Student create(StudentDTO dto) {
        Student student = modelMapper.map(dto, Student.class);
        return repository.save(student);
    }

    @Override
    public Student update(Long id, StudentDTO dto) {
        Student student = findById(id);
        modelMapper.map(dto, student);
        return repository.save(student);
    }

    @Override
    public Student findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found")) ;
    }

    @Override
    public List<Student> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
