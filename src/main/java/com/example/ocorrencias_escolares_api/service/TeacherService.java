package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;

import java.util.List;

public interface TeacherService {
    Teacher create(StudentDTO dto);
    Teacher update(Long id, StudentDTO dto);
    Teacher findById(Long id);
    List<Teacher> findAll();
    void delete(Long id);
}
