package com.example.ocorrencias_escolares_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "courses")
@Data
@EqualsAndHashCode(callSuper = false)
public class Course extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(unique = true, nullable = false)
    private String name;

    @NotBlank(message = "Acronym is mandatory")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String acronym;
}