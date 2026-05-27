package com.example.ocorrencias_escolares_api.entity;

import com.example.ocorrencias_escolares_api.enums.GradeShift;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "grades")
@Data
@EqualsAndHashCode(callSuper = false)
public class Grade extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false)
    private String course;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GradeShift shift;
}