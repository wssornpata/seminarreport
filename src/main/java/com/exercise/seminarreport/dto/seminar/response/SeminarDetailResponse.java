package com.exercise.seminarreport.dto.seminar.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeminarDetailResponse {
    private String timeDuration;
    private String seminar;
    private String duration;
}
