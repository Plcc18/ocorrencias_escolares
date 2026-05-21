package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.TeacherDTO;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeacherService {
    Teacher create(TeacherDTO dto);
    Teacher update(Long id, TeacherDTO dto);
    Teacher findById(Long id);
    Page<Teacher> findAll(Pageable pageable);
    void delete(Long id);
}
