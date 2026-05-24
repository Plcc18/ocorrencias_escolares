package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    Student create(StudentDTO dto);
    Student update(Long id, StudentDTO dto);
    Student findById(Long id);
    List<Student> findAll();
    void delete(Long id);
}
