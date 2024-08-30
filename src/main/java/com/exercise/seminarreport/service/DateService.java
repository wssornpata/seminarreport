package com.exercise.seminarreport.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class DateService {
    private static final LocalTime nineAM = LocalTime.of(9, 0);
    private static final LocalTime twelveAM = LocalTime.of(12, 0);
    private static final LocalTime onePM = LocalTime.of(13, 0);
    private static final LocalTime fourPM = LocalTime.of(16, 0);
    private static final LocalTime fivePM = LocalTime.of(17, 0);

    public DateService() {}

    public static boolean isNineAM(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime().equals(nineAM);
    }

    public static boolean isLunch(LocalDateTime localDateTime) {
        return (localDateTime.toLocalTime().isAfter(twelveAM) || isEqualLunch(localDateTime)) && localDateTime.toLocalTime().isBefore(onePM);
    }

    public static boolean isEqualLunch(LocalDateTime localDateTime) {
        return (localDateTime.toLocalTime().equals(twelveAM));
    }

    public static boolean isNetworkingEvent(LocalDateTime localDateTime) {
        return (localDateTime.getHour() > 16);
    }

    public static boolean checkLastNetworkingEvent(LocalDateTime localDateTime) {
        return (localDateTime.toLocalTime().isAfter(fourPM)) || (localDateTime.toLocalTime().equals(fourPM));
    }

    public static boolean isAfterFivePM(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime().isAfter(fivePM);
    }
    public static LocalDateTime setToAfternoon(LocalDateTime localDateTime) {
        return  localDateTime.with(onePM);
    }

    public static LocalDateTime setToNextDay(LocalDateTime localDateTime) {
        return localDateTime.plusDays(1).with(nineAM);
    }

    public static  LocalDateTime checkWeekend(LocalDateTime localDateTime) {
        while (localDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            localDateTime = localDateTime.plusDays(1);
        }
        return localDateTime;
    }
}
