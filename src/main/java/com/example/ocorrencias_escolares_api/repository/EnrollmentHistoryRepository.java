package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {

    List<EnrollmentHistory> findByStudentIdOrderByStartDateDesc(Long studentId);

    Optional<EnrollmentHistory> findByStudentIdAndEndDateIsNull(Long studentId);

    List<EnrollmentHistory> findByGradeIdAndEndDateIsNull(Long gradeId);

    @Query("""
        SELECT COUNT(eh) FROM EnrollmentHistory eh
        WHERE eh.grade.schoolYear = :schoolYear
          AND eh.endDate IS NULL
          AND eh.grade.gradeLevel < 3
        """)
    long countPromotableStudents(@Param("schoolYear") Integer schoolYear);

    boolean existsByStudentIdAndEndDateIsNull(Long studentId);
}