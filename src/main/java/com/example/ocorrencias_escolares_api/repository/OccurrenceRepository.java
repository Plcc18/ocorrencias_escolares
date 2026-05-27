package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.Occurrence;
import com.example.ocorrencias_escolares_api.enums.OccurrenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface OccurrenceRepository extends JpaRepository<Occurrence, Long> {

    Page<Occurrence> findAll(Pageable pageable);

    Page<Occurrence> findByStudentId(Long studentId, Pageable pageable);

    Page<Occurrence> findByTeacherId(Long teacherId, Pageable pageable);

    boolean existsByStudentId(Long studentId);

    boolean existsByTeacherId(Long teacherId);

    @Query("""
            SELECT o FROM Occurrence o
            WHERE (:studentId IS NULL OR o.student.id = :studentId)
              AND (:teacherId IS NULL OR o.teacher.id = :teacherId)
              AND (:gradeId IS NULL OR o.student.grade.id = :gradeId)
              AND (:occurrenceType IS NULL OR o.occurrenceType = :occurrenceType)
              AND (:startDate IS NULL OR o.occurrenceDate >= :startDate)
              AND (:endDate IS NULL OR o.occurrenceDate <= :endDate)
            """)
    Page<Occurrence> findWithFilters(
            @Param("studentId") Long studentId,
            @Param("teacherId") Long teacherId,
            @Param("gradeId") Long gradeId,
            @Param("occurrenceType") OccurrenceType occurrenceType,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );
}