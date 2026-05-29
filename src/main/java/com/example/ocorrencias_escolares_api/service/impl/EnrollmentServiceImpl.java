package com.example.ocorrencias_escolares_api.service.impl;

import com.example.ocorrencias_escolares_api.dto.*;
import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory;
import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory.EnrollmentReason;
import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.entity.Student;
import com.example.ocorrencias_escolares_api.exception.BusinessException;
import com.example.ocorrencias_escolares_api.exception.ResourceNotFoundException;
import com.example.ocorrencias_escolares_api.repository.EnrollmentHistoryRepository;
import com.example.ocorrencias_escolares_api.repository.GradeRepository;
import com.example.ocorrencias_escolares_api.repository.StudentRepository;
import com.example.ocorrencias_escolares_api.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentHistoryRepository enrollmentRepo;
    private final StudentRepository studentRepo;
    private final GradeRepository gradeRepo;

    public EnrollmentServiceImpl(EnrollmentHistoryRepository enrollmentRepo,
                                 StudentRepository studentRepo,
                                 GradeRepository gradeRepo) {
        this.enrollmentRepo = enrollmentRepo;
        this.studentRepo = studentRepo;
        this.gradeRepo = gradeRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentHistoryDTO> getHistory(Long studentId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Aluno não encontrado com id: " + studentId);
        }
        return enrollmentRepo.findByStudentIdOrderByStartDateDesc(studentId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public EnrollmentHistoryDTO promoteStudent(Long studentId, PromoteStudentDTO dto) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com id: " + studentId));

        Grade targetGrade = gradeRepo.findById(dto.getTargetGradeId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada com id: " + dto.getTargetGradeId()));

        // Valida que não está promovendo para a mesma turma
        if (student.getGrade().getId().equals(targetGrade.getId())) {
            throw new BusinessException("O aluno já está matriculado nesta turma.");
        }

        // Encerra matrícula ativa
        closeActiveEnrollment(student, dto.getNotes());

        // Atualiza turma atual do aluno
        student.setGrade(targetGrade);
        // Sincroniza turno com a nova turma
        student.setShift(targetGrade.getShift());
        studentRepo.save(student);

        // Abre nova matrícula
        EnrollmentHistory newEnrollment = createEnrollment(student, targetGrade,
                dto.getReason() != null ? dto.getReason() : EnrollmentReason.PROMOCAO,
                dto.getNotes());

        return toDTO(newEnrollment);
    }

    @Override
    @Transactional
    public PromotionResultDTO bulkPromote(BulkPromoteDTO dto) {
        Grade sourceGrade = gradeRepo.findById(dto.getSourceGradeId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma de origem não encontrada: " + dto.getSourceGradeId()));

        Grade targetGrade = gradeRepo.findById(dto.getTargetGradeId())
                .orElseThrow(() -> new ResourceNotFoundException("Turma destino não encontrada: " + dto.getTargetGradeId()));

        if (sourceGrade.getId().equals(targetGrade.getId())) {
            throw new BusinessException("Turma de origem e destino não podem ser iguais.");
        }

        // Valida progressão lógica (ex: 1° para 2°, não 1° para 3°)
        if (!dto.getReason().equals(EnrollmentReason.TRANSFERENCIA)) {
            int levelDiff = targetGrade.getGradeLevel() - sourceGrade.getGradeLevel();
            if (levelDiff != 0 && levelDiff != 1) {
                throw new BusinessException(
                        "Promoção inválida: saltos de mais de uma série não são permitidos. " +
                                "Use o motivo TRANSFERENCIA para movimentações especiais."
                );
            }
        }

        // Determina quais alunos promover
        List<Student> candidates;
        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            candidates = studentRepo.findAllById(dto.getStudentIds()).stream()
                    .filter(s -> s.getGrade().getId().equals(sourceGrade.getId()))
                    .toList();
        } else {
            candidates = studentRepo.findByGradeId(sourceGrade.getId()).stream()
                    .filter(s -> "ATIVO".equals(s.getStatus()))
                    .toList();
        }

        if (candidates.isEmpty()) {
            throw new BusinessException("Nenhum aluno ativo encontrado na turma de origem.");
        }

        List<String> promoted = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (Student student : candidates) {
            try {
                closeActiveEnrollment(student, dto.getNotes());
                student.setGrade(targetGrade);
                student.setShift(targetGrade.getShift());
                studentRepo.save(student);
                createEnrollment(student, targetGrade,
                        dto.getReason() != null ? dto.getReason() : EnrollmentReason.PROMOCAO,
                        dto.getNotes());
                promoted.add(student.getName());
            } catch (Exception e) {
                skipped.add(student.getName() + ": " + e.getMessage());
            }
        }

        return new PromotionResultDTO(
                promoted.size(),
                skipped.size(),
                targetGrade.getDisplayName(),
                promoted,
                skipped
        );
    }

    @Override
    @Transactional
    public EnrollmentHistory registerInitialEnrollment(Long studentId, Long gradeId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + studentId));
        Grade grade = gradeRepo.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + gradeId));

        enrollmentRepo.findByStudentIdAndEndDateIsNull(studentId)
                .ifPresent(active -> {
                    throw new BusinessException("Aluno já possui matrícula ativa.");
                });

        return createEnrollment(student, grade, EnrollmentReason.MATRICULA, null);
    }

    @Override
    @Transactional
    public EnrollmentHistory transferEnrollment(Long studentId, Long gradeId, EnrollmentReason reason, String notes) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado: " + studentId));
        Grade grade = gradeRepo.findById(gradeId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma não encontrada: " + gradeId));

        closeActiveEnrollment(student, notes);

        return createEnrollment(
                student,
                grade,
                reason != null ? reason : EnrollmentReason.TRANSFERENCIA,
                notes
        );
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void closeActiveEnrollment(Student student, String notes) {
        enrollmentRepo.findByStudentIdAndEndDateIsNull(student.getId())
                .ifPresent(active -> {
                    active.setEndDate(LocalDate.now());
                    if (notes != null && active.getNotes() == null) {
                        active.setNotes(notes);
                    }
                    enrollmentRepo.saveAndFlush(active);
                });
    }

    private EnrollmentHistory createEnrollment(Student student, Grade grade,
                                               EnrollmentReason reason, String notes) {
        EnrollmentHistory eh = new EnrollmentHistory();
        eh.setStudent(student);
        eh.setGrade(grade);
        eh.setStartDate(LocalDate.now());
        eh.setEndDate(null);
        eh.setReason(reason);
        eh.setNotes(notes);
        return enrollmentRepo.save(eh);
    }

    private EnrollmentHistoryDTO toDTO(EnrollmentHistory eh) {
        EnrollmentHistoryDTO dto = new EnrollmentHistoryDTO();
        dto.setId(eh.getId());
        dto.setStudentId(eh.getStudent().getId());
        dto.setStudentName(eh.getStudent().getName());
        dto.setGradeId(eh.getGrade().getId());
        dto.setGradeName(eh.getGrade().getDisplayName());
        dto.setCourseAcronym(eh.getGrade().getCourse().getAcronym());
        dto.setStartDate(eh.getStartDate());
        dto.setEndDate(eh.getEndDate());
        dto.setReason(eh.getReason());
        dto.setNotes(eh.getNotes());
        dto.setActive(eh.getEndDate() == null);
        dto.setCreatedAt(eh.getCreatedAt());
        return dto;
    }
}
