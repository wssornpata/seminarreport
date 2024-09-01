package com.exercise.seminarreport.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import static com.exercise.seminarreport.constants.TimeConstants.*;

@Service
public class TimeService {

    public static boolean isNineAM(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime().equals(NINE_AM);
    }

    public static boolean isLunch(LocalDateTime localDateTime) {
        return (localDateTime.toLocalTime().isAfter(TWELVE_AM) || isEqualLunch(localDateTime)) && localDateTime.toLocalTime().isBefore(ONE_PM);
    }

    public static boolean isEqualLunch(LocalDateTime localDateTime) {
        return (localDateTime.toLocalTime().equals(TWELVE_AM));
    }

    public static boolean isNetworkingEvent(LocalDateTime localDateTime) {
        return (localDateTime.getHour() > 16);
    }

    public static boolean checkLastNetworkingEvent(LocalDateTime localDateTime) {
        return (localDateTime.toLocalTime().isAfter(FOUR_PM)) || (localDateTime.toLocalTime().equals(FOUR_PM));
    }

    public static boolean isAfterFivePM(LocalDateTime localDateTime) {
        return localDateTime.toLocalTime().isAfter(FIVE_PM);
    }
    public static LocalDateTime setToAfternoon(LocalDateTime localDateTime) {
        return  localDateTime.with(ONE_PM);
    }

    public static LocalDateTime setToNextDay(LocalDateTime localDateTime) {
        return localDateTime.plusDays(1).with(NINE_AM);
    }

    public static  LocalDateTime checkWeekend(LocalDateTime localDateTime) {
        while (localDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            localDateTime = localDateTime.plusDays(1);
        }
        return localDateTime;
    }
}
