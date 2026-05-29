package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.GradeDTO;
import com.example.ocorrencias_escolares_api.entity.Course;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.GradeRepository;
import com.example.ocorrencias_escolares_api.repository.StudentRepository;
import com.example.ocorrencias_escolares_api.service.CourseService;
import com.example.ocorrencias_escolares_api.service.GradeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {

    private final GradeRepository repository;
    private final StudentRepository studentRepository;
    private final CourseService courseService;

    public GradeServiceImpl(GradeRepository repository,
                            StudentRepository studentRepository,
                            CourseService courseService) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.courseService = courseService;
    }

    @Override
    @Transactional
    public Grade create(GradeDTO dto) {
        Course course = courseService.findById(dto.getCourseId());
        if (repository.existsByCourseIdAndGradeLevelAndSchoolYearAndShift(
                dto.getCourseId(), dto.getGradeLevel(), dto.getSchoolYear(), dto.getShift())) {
            throw new BusinessException(
                    "Já existe uma turma para " + dto.getGradeLevel() + "º " +
                            course.getAcronym() + " - " + dto.getSchoolYear() +
                            " no turno " + dto.getShift().getLabel()
            );
        }
        Grade grade = new Grade();
        grade.setGradeLevel(dto.getGradeLevel());
        grade.setSchoolYear(dto.getSchoolYear());
        grade.setCourse(course);
        grade.setShift(dto.getShift());
        return repository.save(grade);
    }

    @Override
    @Transactional
    public Grade update(Long id, GradeDTO dto) {
        Grade grade = findById(id);
        Course course = courseService.findById(dto.getCourseId());
        if (repository.existsByCourseIdAndGradeLevelAndSchoolYearAndShiftAndIdNot(
                dto.getCourseId(), dto.getGradeLevel(), dto.getSchoolYear(), dto.getShift(), id)) {
            throw new BusinessException(
                    "Já existe uma turma para " + dto.getGradeLevel() + "º " +
                            course.getAcronym() + " - " + dto.getSchoolYear() +
                            " no turno " + dto.getShift().getLabel()
            );
        }
        grade.setGradeLevel(dto.getGradeLevel());
        grade.setSchoolYear(dto.getSchoolYear());
        grade.setCourse(course);
        grade.setShift(dto.getShift());
        return repository.save(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public Grade findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDTO> findAllWithStudentCount() {
        return repository.findAll().stream()
                .map(g -> toDTO(g, studentRepository.countByGradeId(g.getId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countStudents(Long gradeId) {
        return studentRepository.countByGradeId(gradeId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Turma não encontrada com id: " + id);
        }
        if (studentRepository.existsByGradeId(id)) {
            throw new BusinessException(
                    "Não é possível remover a turma pois existem alunos vinculados. " +
                            "Remova ou transfira os alunos antes de excluir a turma.");
        }
        repository.deleteById(id);
    }

    public GradeDTO toDTO(Grade g, long studentCount) {
        GradeDTO dto = new GradeDTO();
        dto.setId(g.getId());
        dto.setGradeLevel(g.getGradeLevel());
        dto.setSchoolYear(g.getSchoolYear());
        dto.setCourseId(g.getCourse().getId());
        dto.setCourseName(g.getCourse().getName());
        dto.setCourseAcronym(g.getCourse().getAcronym());
        dto.setShift(g.getShift());
        dto.setDisplayName(g.getDisplayName());
        dto.setStudentCount(studentCount);
        dto.setCreatedAt(g.getCreatedAt());
        dto.setUpdatedAt(g.getUpdatedAt());
        return dto;
    }
}