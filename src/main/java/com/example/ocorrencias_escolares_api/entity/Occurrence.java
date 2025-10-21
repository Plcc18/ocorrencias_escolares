package com.example.ocorrencias_escolares_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "occurrences")
@Data
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Date is mandatory")
    private LocalDate occurrenceDate;

    @NotNull(message = "Student is mandatory")
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @NotBlank(message = "Type is mandatory")
    private String occurrenceType;
}
