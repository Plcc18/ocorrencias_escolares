package com.example.ocorrencias_escolares_api.service;

import com.example.ocorrencias_escolares_api.dto.BulkPromoteDTO;
import com.example.ocorrencias_escolares_api.dto.EnrollmentHistoryDTO;
import com.example.ocorrencias_escolares_api.dto.PromoteStudentDTO;
import com.example.ocorrencias_escolares_api.dto.PromotionResultDTO;
import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory;
import com.example.ocorrencias_escolares_api.entity.EnrollmentHistory.EnrollmentReason;

import java.util.List;

public interface EnrollmentService {

    List<EnrollmentHistoryDTO> getHistory(Long studentId);

    EnrollmentHistoryDTO promoteStudent(Long studentId, PromoteStudentDTO dto);

    PromotionResultDTO bulkPromote(BulkPromoteDTO dto);

    EnrollmentHistory registerInitialEnrollment(Long studentId, Long gradeId);

    EnrollmentHistory transferEnrollment(Long studentId, Long gradeId, EnrollmentReason reason, String notes);
}
