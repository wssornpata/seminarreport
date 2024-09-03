package com.exercise.seminarreport.dto.seminar.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeminarDetailResponse {
//    private String time;
    private LocalTime time;
    private String seminar;
    private String duration;
}
