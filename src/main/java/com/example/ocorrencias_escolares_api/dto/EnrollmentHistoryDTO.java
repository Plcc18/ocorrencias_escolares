package com.example.ocorrencias_escolares_api.dto;

import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory.EnrollmentReason;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class EnrollmentHistoryDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long studentId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String studentName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long gradeId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String gradeName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String courseAcronym;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate startDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate endDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private EnrollmentReason reason;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String notes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean active;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}