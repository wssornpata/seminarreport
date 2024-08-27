package com.exercise.seminarreport.dto.seminar.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeminarDetailResponse {
    private String timeDuration;
    private String seminar;

    public String getSeminar() {
        return seminar;
    }

    public String getTimeDuration() {
        return timeDuration;
    }
}
