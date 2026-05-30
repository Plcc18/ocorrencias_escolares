package com.example.ocorrencias_escolares_api.entity;

import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

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

    @NotNull(message = "Grade is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @NotNull(message = "Type is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "occurrence_type", nullable = false, length = 50)
    private OccurrenceType occurrenceType;
}
