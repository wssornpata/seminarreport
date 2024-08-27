package com.exercise.seminarreport.dto.seminar.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeminarResponse {
    private String date;
    private List<SeminarDetailResponse> agendas;

    public String getDate() {
        return date;
    }

    public List<SeminarDetailResponse> getAgendas() {
        return agendas;
    }
}
