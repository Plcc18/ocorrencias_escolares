package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    Student create(StudentDTO dto);
    Student update(Long id, StudentDTO dto);
    Student findById(Long id);
    Page<Student> findAll(Pageable pageable);
    void delete(Long id);
}
