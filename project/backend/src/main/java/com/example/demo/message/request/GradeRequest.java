package com.example.demo.message.request;

import jakarta.validation.constraints.NotNull;

public class GradeRequest {
    @NotNull
    private Long studentId;

    @NotNull
    private Long subjectId;

    @NotNull
    private Double value;

    private String description;

    public GradeRequest() {
    }

    public GradeRequest(Long studentId, Long subjectId, Double value, String description) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.value = value;
        this.description = description;
    }

    public Long getStudentId() {
        return this.studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSubjectId() {
        return this.subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
