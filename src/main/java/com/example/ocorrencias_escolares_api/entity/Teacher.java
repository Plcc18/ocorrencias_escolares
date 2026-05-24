package com.example.ocorrencias_escolares_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "teachers")
@Data
@EqualsAndHashCode(callSuper = false)
public class Teacher extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is mandatory")
    @Size(max = 100)
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Subject is mandatory")
    @Size(max = 50)
    @Column(nullable = false)
    private String subject;
}