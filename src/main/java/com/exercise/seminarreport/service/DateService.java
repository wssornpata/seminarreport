package com.exercise.seminarreport.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DateService {
    public DateService() {}

    public boolean isNineAM(LocalDateTime localDateTime) {
        return localDateTime.getHour() == 9 && localDateTime.getMinute() == 0;
    }
}
