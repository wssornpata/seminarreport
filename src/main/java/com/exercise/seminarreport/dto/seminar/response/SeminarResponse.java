package com.exercise.seminarreport.dto.seminar.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeminarResponse {
    private String date;
//    private LocalDate date;
    private List<SeminarDetailResponse> agendas;
}
