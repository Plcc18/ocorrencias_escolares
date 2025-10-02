package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
