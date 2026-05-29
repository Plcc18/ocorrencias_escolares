package com.example.ocorrencias_escolares_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class PromotionResultDTO {
    private int promoted;
    private int skipped;
    private String targetGradeName;
    private List<String> promotedStudentNames;
    private List<String> skippedReasons;

    public PromotionResultDTO(int promoted, int skipped, String targetGradeName,
                              List<String> promotedStudentNames, List<String> skippedReasons) {
        this.promoted = promoted;
        this.skipped = skipped;
        this.targetGradeName = targetGradeName;
        this.promotedStudentNames = promotedStudentNames;
        this.skippedReasons = skippedReasons;
    }
}