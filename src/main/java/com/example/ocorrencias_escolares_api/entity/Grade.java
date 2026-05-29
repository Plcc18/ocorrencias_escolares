package com.example.ocorrencias_escolares_api.entity;

import com.example.ocorrencias_escolares_api.enums.GradeShift;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(
        name = "grades",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_grades_course_level_year_shift",
                columnNames = {"course_id", "grade_level", "school_year", "shift"}
        )
)
@Data
@EqualsAndHashCode(callSuper = false)
public class Grade extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Grade level is mandatory")
    @Min(value = 1, message = "Grade level must be between 1 and 3")
    @Max(value = 3, message = "Grade level must be between 1 and 3")
    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @NotNull(message = "School year is mandatory")
    @Min(value = 2000, message = "School year must be >= 2000")
    @Max(value = 2100, message = "School year must be <= 2100")
    @Column(name = "school_year", nullable = false)
    private Integer schoolYear;

    @NotNull(message = "Course is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull(message = "Shift is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GradeShift shift;

    @Transient
    public String getDisplayName() {
        if (course == null) return gradeLevel + "º - " + schoolYear;
        String base = gradeLevel + "º " + course.getAcronym() + " - " + schoolYear;
        if (shift != null && shift != GradeShift.MANHA) {
            return base + " (" + shift.getLabel() + ")";
        }
        return base;
    }
}