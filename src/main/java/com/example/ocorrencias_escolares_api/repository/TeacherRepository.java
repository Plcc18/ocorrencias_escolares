package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Page<Teacher> findAll(Pageable pageable);
    Optional<Teacher> findByEmail(String email);
    boolean existsByEmail(String email);
}
