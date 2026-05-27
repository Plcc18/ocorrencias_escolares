package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.GradeDTO;
import com.example.ocorrencias_escolares_api.entity.Grade;

import java.util.List;

public interface GradeService {
    Grade create(GradeDTO dto);
    Grade update(Long id, GradeDTO dto);
    Grade findById(Long id);
    List<Grade> findAll();
    List<GradeDTO> findAllWithStudentCount();
    long countStudents(Long gradeId);
    void delete(Long id);
}