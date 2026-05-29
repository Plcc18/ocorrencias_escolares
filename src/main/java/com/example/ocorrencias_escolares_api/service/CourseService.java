package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.CourseDTO;
import com.example.ocorrencias_escolares_api.entity.Course;

import java.util.List;

public interface CourseService {
    Course create(CourseDTO dto);
    Course update(Long id, CourseDTO dto);
    Course findById(Long id);
    List<CourseDTO> findAllWithGradeCount();
    void delete(Long id);
}