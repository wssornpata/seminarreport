package com.exercise.seminarreport.dto.seminar.response;

import lombok.Data;

import java.time.LocalTime;

@Data
public class SeminarDetailResponse {
    private LocalTime timeDuration;
    private String seminar;
}
