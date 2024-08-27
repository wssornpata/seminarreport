package com.exercise.seminarreport.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class DateService {
    public DateService() {}

    public boolean isNineAM(LocalDateTime localDateTime) {
        LocalTime nineAM = LocalTime.of(9, 0);
        return localDateTime.toLocalTime().equals(nineAM);
    }

    public boolean isLunch(LocalDateTime newTime) {
        LocalTime startLunch = LocalTime.of(12, 0);
        LocalTime endLunch = LocalTime.of(13, 0);
        return newTime.toLocalTime().isAfter(startLunch) && newTime.toLocalTime().isBefore(endLunch);
    }

    public boolean isAfterFivePM(LocalDateTime localDateTime) {
        LocalTime fivePM = LocalTime.of(17, 0);
        return localDateTime.toLocalTime().equals(fivePM);
    }

    public LocalDateTime setToAfternoon(LocalDateTime seminarDateTime) {
        return  seminarDateTime.withHour(13).withMinute(0);
    }

    public LocalDateTime setToNextDay(LocalDateTime seminarDateTime) {
        return seminarDateTime.plusDays(1).withHour(9).withMinute(0);
    }

    boolean isNetworkingEvent(LocalDateTime newTime) {
        return (newTime.getHour() > 16);
    }

    public  LocalDateTime checkWeekend(LocalDateTime localDateTime) {
        while (localDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            localDateTime = localDateTime.plusDays(1);
        }
        return localDateTime;
    }

    public static String changeThaiBuddhistFormat(LocalDateTime dateTime) {
        ThaiBuddhistDate thaiBuddhistDate = ThaiBuddhistDate.from(dateTime);
        ThaiBuddhistDate.from(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(thaiBuddhistDate);
    }
}
