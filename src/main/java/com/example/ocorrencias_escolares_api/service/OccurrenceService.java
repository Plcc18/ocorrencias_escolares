package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.dto.OccurrenceFilterDTO;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OccurrenceService {
    Occurrence create(OccurrenceDTO dto);
    Occurrence update(Long id, OccurrenceDTO dto);
    Occurrence findById(Long id);
    Page<Occurrence> findAll(Pageable pageable);
    Page<Occurrence> findWithFilters(OccurrenceFilterDTO filter, Pageable pageable);
    void delete(Long id);
}
