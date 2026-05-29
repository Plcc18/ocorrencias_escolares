package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.StudentDTO;
import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory.EnrollmentReason;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.OccurrenceRepository;
import com.example.ocorrencias_escolares_api.repository.StudentRepository;
import com.example.ocorrencias_escolares_api.service.EnrollmentService;
import com.example.ocorrencias_escolares_api.service.GradeService;
import com.example.ocorrencias_escolares_api.service.StudentService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;
    private final OccurrenceRepository occurrenceRepository;
    private final GradeService gradeService;
    private final EnrollmentService enrollmentService;

    public StudentServiceImpl(StudentRepository repository,
                              OccurrenceRepository occurrenceRepository,
                              GradeService gradeService,
                              @Lazy EnrollmentService enrollmentService) {
        this.repository = repository;
        this.occurrenceRepository = occurrenceRepository;
        this.gradeService = gradeService;
        this.enrollmentService = enrollmentService;
    }

    @Override
    @Transactional
    public Student create(StudentDTO dto) {
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email já cadastrado para outro aluno: " + dto.getEmail());
        }
        if (repository.existsByEnrollment(dto.getEnrollment())) {
            throw new BusinessException("Matrícula já cadastrada: " + dto.getEnrollment());
        }
        Grade grade = gradeService.findById(dto.getGradeId());
        Student student = new Student();
        fillFromDTO(student, dto, grade);
        student = repository.save(student);

        // Registra matrícula inicial no histórico
        enrollmentService.registerInitialEnrollment(student.getId(), grade.getId());

        return student;
    }

    @Override
    @Transactional
    public Student update(Long id, StudentDTO dto) {
        Student student = findById(id);

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            repository.findByEmail(dto.getEmail())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new BusinessException("Email já cadastrado para outro aluno: " + dto.getEmail());
                    });
        }
        if (repository.existsByEnrollmentAndIdNot(dto.getEnrollment(), id)) {
            throw new BusinessException("Matrícula já cadastrada: " + dto.getEnrollment());
        }

        Grade grade = gradeService.findById(dto.getGradeId());

        // Se a turma mudou, use o serviço de promoção para manter histórico
        // Mudanças de turma via StudentService (edição direta) são tratadas como
        // TRANSFERENCIA silenciosa. Para promoção formal, use /api/enrollments.
        boolean gradeChanged = !student.getGrade().getId().equals(grade.getId());

        fillFromDTO(student, dto, grade);
        student = repository.save(student);

        if (gradeChanged) {
            // Atualiza histórico: encerra atual e abre nova como TRANSFERENCIA
            enrollmentService.transferEnrollment(student.getId(), grade.getId(), EnrollmentReason.TRANSFERENCIA, dto.getNotes());
        }

        return student;
    }

    @Override
    @Transactional(readOnly = true)
    public Student findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Student> findAll(String name, Long gradeId, String status, Pageable pageable) {
        return repository.findWithFilters(
                (name != null && name.isBlank()) ? null : name,
                gradeId,
                (status != null && status.isBlank()) ? null : status,
                pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findByGradeId(Long gradeId) {
        return repository.findByGradeId(gradeId);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno não encontrado com id: " + id);
        }
        if (occurrenceRepository.existsByStudentId(id)) {
            throw new BusinessException(
                    "Não é possível remover o aluno pois existem ocorrências vinculadas a ele. " +
                            "Remova as ocorrências antes de excluir o aluno.");
        }
        repository.deleteById(id);
    }

    private void fillFromDTO(Student student, StudentDTO dto, Grade grade) {
        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setEnrollment(dto.getEnrollment());
        student.setGrade(grade);
        student.setShift(grade.getShift());
        student.setStatus(dto.getStatus() != null ? dto.getStatus() : "ATIVO");
        student.setBirthDate(dto.getBirthDate());
        student.setGuardian(dto.getGuardian());
        student.setGuardianPhone(dto.getGuardianPhone());
        student.setGuardianEmail(dto.getGuardianEmail());
        student.setNotes(dto.getNotes());
    }
}
