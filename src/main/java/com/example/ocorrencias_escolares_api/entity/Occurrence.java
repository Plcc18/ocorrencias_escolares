package com.example.ocorrencias_escolares_api.entity;

import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * Ocorrência escolar.
 *
 * IMPORTANTE: grade_id é gravado como SNAPSHOT no momento do registro.
 * Isso garante que mesmo após o aluno mudar de turma, o histórico
 * continua exibindo a turma correta da época da ocorrência.
 *
 * NÃO use o.getStudent().getGrade() para exibir a turma da ocorrência —
 * use o.getGrade() que é o snapshot imutável.
 */
@Entity
@Table(name = "occurrences")
@Data
@EqualsAndHashCode(callSuper = false)
public class Occurrence extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description is mandatory")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @NotNull(message = "Date is mandatory")
    @Column(name = "occurrence_date", nullable = false)
    private LocalDate occurrenceDate;

    @NotNull(message = "Student is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Teacher is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @NotNull(message = "Type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "occurrence_type", nullable = false, length = 50)
    private OccurrenceType occurrenceType;
}