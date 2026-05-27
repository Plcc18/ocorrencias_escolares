package com.example.ocorrencias_escolares_api.repository;

import com.example.ocorrencias_escolares_api.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEnrollment(String enrollment);
    boolean existsByEnrollmentAndIdNot(String enrollment, Long id);
    boolean existsByGradeId(Long gradeId);
    long countByGradeId(Long gradeId);
    List<Student> findByGradeId(Long gradeId);

    @Query("""
            SELECT s FROM Student s
            WHERE (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:gradeId IS NULL OR s.grade.id = :gradeId)
              AND (:status IS NULL OR s.status = :status)
            """)
    Page<Student> findWithFilters(
            @Param("name") String name,
            @Param("gradeId") Long gradeId,
            @Param("status") String status,
            Pageable pageable
    );
}