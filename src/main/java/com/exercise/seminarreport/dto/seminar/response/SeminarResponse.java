package com.exercise.seminarreport.dto.seminar.response;

import lombok.Data;

import java.util.List;

@Data
public class SeminarResponse {
    private String day;
    private List<SeminarDetailResponse> dayDetails;
}
