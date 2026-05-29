package com.example.ocorrencias_escolares_api.enums;

public enum GradeShift {
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite"),
    INTEGRAL("Integral");

    private final String label;

    GradeShift(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}