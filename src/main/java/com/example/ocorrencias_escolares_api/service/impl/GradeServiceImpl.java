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
        if (repository.existsByName(dto.getName())) {
            throw new BusinessException("Já existe uma turma com o nome: " + dto.getName());
        }
        Course course = courseService.findById(dto.getCourseId());
        Grade grade = new Grade();
        grade.setName(dto.getName());
        grade.setCourse(course);
        grade.setShift(dto.getShift());
        return repository.save(grade);
    }

    @Override
    @Transactional
    public Grade update(Long id, GradeDTO dto) {
        Grade grade = findById(id);
        if (repository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new BusinessException("Já existe uma turma com o nome: " + dto.getName());
        }
        Course course = courseService.findById(dto.getCourseId());
        grade.setName(dto.getName());
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
        return repository.findAll().stream().map(g -> toDTO(g, studentRepository.countByGradeId(g.getId()))).toList();
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
                    "Não é possível remover a turma pois existem alunos vinculados a ela. " +
                            "Remova ou transfira os alunos antes de excluir a turma.");
        }
        repository.deleteById(id);
    }

    private GradeDTO toDTO(Grade g, long studentCount) {
        GradeDTO dto = new GradeDTO();
        dto.setId(g.getId());
        dto.setName(g.getName());
        dto.setCourseId(g.getCourse().getId());
        dto.setCourseName(g.getCourse().getName());
        dto.setCourseAcronym(g.getCourse().getAcronym());
        dto.setShift(g.getShift());
        dto.setStudentCount(studentCount);
        dto.setCreatedAt(g.getCreatedAt());
        dto.setUpdatedAt(g.getUpdatedAt());
        return dto;
    }
}