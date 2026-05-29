package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.Grade;
import com.example.ocorrencias_escolares_api.enums.GradeShift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    boolean existsByCourseId(Long courseId);

    List<Grade> findByCourseId(Long courseId);

    // Verifica duplicidade ao criar
    boolean existsByCourseIdAndGradeLevelAndSchoolYearAndShift(
            Long courseId, Integer gradeLevel, Integer schoolYear, GradeShift shift);

    // Verifica duplicidade ao editar (exclui o próprio registro)
    boolean existsByCourseIdAndGradeLevelAndSchoolYearAndShiftAndIdNot(
            Long courseId, Integer gradeLevel, Integer schoolYear, GradeShift shift, Long id);

    List<Grade> findBySchoolYear(Integer schoolYear);
}