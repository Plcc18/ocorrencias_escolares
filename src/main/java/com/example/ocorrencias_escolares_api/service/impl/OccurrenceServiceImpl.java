package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.OccurrenceDTO;
import com.example.ocorrencias_escolares_api.dto.OccurrenceFilterDTO;
import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.entity.Teacher;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.service.OccurrenceService;
import com.example.ocorrencias_escolares_api.service.StudentService;
import com.example.ocorrencias_escolares_api.service.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OccurrenceServiceImpl implements OccurrenceService {

    private final OccurrenceRepository repository;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public OccurrenceServiceImpl(OccurrenceRepository repository,
                                 StudentService studentService,
                                 TeacherService teacherService) {
        this.repository = repository;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    @Override
    @Transactional
    public Occurrence create(OccurrenceDTO dto) {
        Student student = studentService.findById(dto.getStudentId());
        Teacher teacher = teacherService.findById(dto.getTeacherId());

        Occurrence occurrence = new Occurrence();
        occurrence.setDescription(dto.getDescription());
        occurrence.setOccurrenceDate(dto.getOccurrenceDate());
        occurrence.setOccurrenceType(dto.getOccurrenceType());
        occurrence.setStudent(student);
        occurrence.setTeacher(teacher);

        return repository.save(occurrence);
    }

    @Override
    @Transactional
    public Occurrence update(Long id, OccurrenceDTO dto) {
        Occurrence occurrence = findById(id);

        occurrence.setDescription(dto.getDescription());
        occurrence.setOccurrenceDate(dto.getOccurrenceDate());
        occurrence.setOccurrenceType(dto.getOccurrenceType());

        if (dto.getStudentId() != null) {
            occurrence.setStudent(studentService.findById(dto.getStudentId()));
        }
        if (dto.getTeacherId() != null) {
            occurrence.setTeacher(teacherService.findById(dto.getTeacherId()));
        }

        return repository.save(occurrence);
    }

    @Override
    @Transactional(readOnly = true)
    public Occurrence findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ocorrência não encontrada com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Occurrence> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Occurrence> findWithFilters(OccurrenceFilterDTO filter, Pageable pageable) {
        return repository.findWithFilters(
                filter.getStudentId(),
                filter.getTeacherId(),
                filter.getOccurrenceType(),
                filter.getStartDate(),
                filter.getEndDate(),
                pageable
        );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Ocorrência não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }
}
