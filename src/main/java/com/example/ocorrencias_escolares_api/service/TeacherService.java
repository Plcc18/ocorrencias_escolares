package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;

import java.util.List;

public interface TeacherService {
    Teacher create(TeacherDTO dto);
    Teacher update(Long id, TeacherDTO dto);
    Teacher findById(Long id);
    List<Teacher> findAll();
    void delete(Long id);
}
