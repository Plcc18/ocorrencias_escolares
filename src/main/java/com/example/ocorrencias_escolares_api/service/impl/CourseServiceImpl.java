package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.CourseDTO;
import com.example.ocorrencias_escolares_api.entity.Course;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.CourseRepository;
import com.example.ocorrencias_escolares_api.repository.GradeRepository;
import com.example.ocorrencias_escolares_api.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;
    private final GradeRepository gradeRepository;

    public CourseServiceImpl(CourseRepository repository, GradeRepository gradeRepository) {
        this.repository = repository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    @Transactional
    public Course create(CourseDTO dto) {
        if (repository.existsByName(dto.getName())) {
            throw new BusinessException("Já existe um curso com o nome: " + dto.getName());
        }
        if (repository.existsByAcronym(dto.getAcronym())) {
            throw new BusinessException("Já existe um curso com a sigla: " + dto.getAcronym());
        }
        Course course = new Course();
        course.setName(dto.getName());
        course.setAcronym(dto.getAcronym().toUpperCase());
        return repository.save(course);
    }

    @Override
    @Transactional
    public Course update(Long id, CourseDTO dto) {
        Course course = findById(id);
        if (repository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new BusinessException("Já existe um curso com o nome: " + dto.getName());
        }
        if (repository.existsByAcronymAndIdNot(dto.getAcronym(), id)) {
            throw new BusinessException("Já existe um curso com a sigla: " + dto.getAcronym());
        }
        course.setName(dto.getName());
        course.setAcronym(dto.getAcronym().toUpperCase());
        return repository.save(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Course findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findAllWithGradeCount() {
        return repository.findAll().stream().map(c -> {
            CourseDTO dto = new CourseDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setAcronym(c.getAcronym());
            dto.setGradeCount((long) gradeRepository.findByCourseId(c.getId()).size());
            dto.setCreatedAt(c.getCreatedAt());
            dto.setUpdatedAt(c.getUpdatedAt());
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Curso não encontrado com id: " + id);
        }
        if (gradeRepository.existsByCourseId(id)) {
            throw new BusinessException(
                    "Não é possível remover o curso pois existem turmas vinculadas a ele. " +
                            "Remova ou transfira as turmas antes de excluir o curso.");
        }
        repository.deleteById(id);
    }
}