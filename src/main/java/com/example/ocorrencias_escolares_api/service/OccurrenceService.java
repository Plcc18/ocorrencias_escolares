package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.entity.Student;

import java.util.List;

public interface OccurrenceService {
    Occurrence create(OccurrenceDTO dto);
    Occurrence update(Long id, OccurrenceDTO dto);
    Occurrence findById(Long id);
    List<Occurrence> findAll();
    void delete(Long id);
}
