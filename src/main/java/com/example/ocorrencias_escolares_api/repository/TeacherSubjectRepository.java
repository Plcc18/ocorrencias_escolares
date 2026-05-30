package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.TeacherSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherSubjectRepository extends JpaRepository<TeacherSubject, Long> {
    List<TeacherSubject> findByTeacherId(Long teacherId);
    void deleteByTeacherId(Long teacherId);
}
