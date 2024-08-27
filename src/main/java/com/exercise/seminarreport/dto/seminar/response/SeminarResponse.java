package com.exercise.seminarreport.dto.seminar.response;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

@Data
public class SeminarResponse {
    private LocalDate date;
    private List<SeminarDetailResponse> agendas;
}
