package com.example.ocorrencias_escolares_api.entity;

import com.example.ocorrencias_escolares_api.enums.GradeShift;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "students")
@Data
@EqualsAndHashCode(callSuper = false)
public class Student extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @Size(max = 100)
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Enrollment is mandatory")
    @Size(max = 30)
    @Column(unique = true, nullable = false)
    private String enrollment;

    @NotNull(message = "Grade is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @NotBlank(message = "Course is mandatory")
    @Size(max = 100)
    @Column(nullable = false)
    private String course;

    @NotNull(message = "Shift is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GradeShift shift;

    @Column(nullable = false, length = 10)
    private String status = "ATIVO";

    @Column(name = "birth_date")
    private String birthDate;

    @Size(max = 100)
    @Column(name = "guardian")
    private String guardian;

    @Size(max = 20)
    @Column(name = "guardian_phone")
    private String guardianPhone;

    @Size(max = 100)
    @Column(name = "guardian_email")
    private String guardianEmail;

    @Column(columnDefinition = "TEXT")
    private String notes;
}