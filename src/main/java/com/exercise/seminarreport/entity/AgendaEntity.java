package com.exercise.seminarreport.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgendaEntity {
    private String agendaName;
    private Integer agendaDuration;
}
