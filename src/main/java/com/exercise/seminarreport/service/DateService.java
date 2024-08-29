package com.exercise.seminarreport.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DateService {
    public DateService() {}

    public boolean isNineAM(LocalDateTime localDateTime) {
        LocalTime nineAM = LocalTime.of(9, 0);
        return localDateTime.toLocalTime().equals(nineAM);
    }

    public boolean isLunch(LocalDateTime localDateTime) {
        LocalTime startLunch = LocalTime.of(12, 0);
        LocalTime endLunch = LocalTime.of(13, 0);
        return (localDateTime.toLocalTime().isAfter(startLunch) || localDateTime.toLocalTime().equals(startLunch)) && localDateTime.toLocalTime().isBefore(endLunch);
    }

    boolean isNetworkingEvent(LocalDateTime localDateTime) {
        return (localDateTime.getHour() > 16);
    }

    public boolean checkLastNetworkingEvent(LocalDateTime localDateTime) {
        LocalTime lastNetworkingEvent = LocalTime.of(16, 0);
        return (localDateTime.toLocalTime().isAfter(lastNetworkingEvent)) || (localDateTime.toLocalTime().equals(lastNetworkingEvent));
    }

    public boolean isAfterFivePM(LocalDateTime localDateTime) {
        LocalTime fivePM = LocalTime.of(17, 0);
        return localDateTime.toLocalTime().isAfter(fivePM);
    }
    public LocalDateTime setToAfternoon(LocalDateTime localDateTime) {
        return  localDateTime.withHour(13).withMinute(0);
    }

    public LocalDateTime setToNextDay(LocalDateTime localDateTime) {
        return localDateTime.plusDays(1).withHour(9).withMinute(0);
    }

    public  LocalDateTime checkWeekend(LocalDateTime localDateTime) {
        while (localDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            localDateTime = localDateTime.plusDays(1);
        }
        return localDateTime;
    }
}
