package com.exercise.seminarreport.service;

import com.exercise.seminarreport.SeminarreportApplication;
import com.exercise.seminarreport.dto.seminar.response.SeminarDetailResponse;
import com.exercise.seminarreport.dto.seminar.response.SeminarResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ThaiBuddhistDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SeminarService {

    private static final Logger logger = LogManager.getLogger(SeminarreportApplication.class);
    private final DateService dateService;

    private String minRegex = "(\\d+)min";
    private String datePattern = "yyyy-MM-dd";
    private String timePattern = "hh:mma";

    private final DateTimeFormatter dateTimeFormatter;
    private final DateTimeFormatter timeFormatter;
    private final Pattern minutePattern;
    private Matcher minuteMatcher;

    public SeminarService(DateService dateService) {
        this.dateService = dateService;
        this.minutePattern = Pattern.compile(minRegex);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
        this.timeFormatter = DateTimeFormatter.ofPattern(timePattern);
    }

    public List<SeminarResponse> getSeminarResponse(MultipartFile file) {
        if (file.isEmpty()) {
            throw new Error("Seminar service: File is empty.");
        }
        logger.info("Get File.");

        List<SeminarResponse> responseList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            logger.info("Read File.");
            LocalDateTime seminarDateTime = null;
            SeminarResponse response = new SeminarResponse();
            List<SeminarDetailResponse> detailResponseList = new ArrayList<>();
            String line;
            int countDay = 0;
            while ((line = reader.readLine()) != null) {
                if (countDay == 0) {
                    String date = line;
                    try {
                        LocalDate parsedDate = LocalDate.parse(date, dateTimeFormatter);
                        seminarDateTime = parsedDate.atTime(9, 0);
                        countDay++;
                        logger.info("Get Start Seminar Date From File.");
                    } catch (DateTimeParseException e) {
                        throw new Error("Seminar service: Invalid date format.");
                    }
                }else {
                    if (isMatchPattern(line))  {
                        if (dateService.isNineAM(seminarDateTime)) {
                            response.setDay(getDayFormat(seminarDateTime, countDay));
                            response.setDayDetails(detailResponseList);
                            responseList.add(response);
                            logger.info("Write "+response.getDay()+" "+ seminarDateTime.getDayOfWeek());
                            response = new SeminarResponse();
                            if(countDay > 1) {
                                detailResponseList = new ArrayList<>();
                            }
                            countDay++;
                        }

                        line = line.trim();
                        int minute = Integer.parseInt(minuteMatcher.group(1));
                        LocalDateTime newTime = seminarDateTime.plusMinutes(minute);

                        if (isLunch(newTime)) {
                            detailResponseList.add(appendSeminarDetail(seminarDateTime, line));
                            detailResponseList.add(appendLunch());
                            seminarDateTime = setToAfternoon(seminarDateTime);
                        } else if (isNetworkingEvent(newTime)) {
                            if (isAfterFivePM(newTime)) {
                                detailResponseList.add(appendNetworkingEvent(seminarDateTime));
                            } else {
                                detailResponseList.add(appendSeminarDetail(seminarDateTime, line));
                                detailResponseList.add(appendNetworkingEvent(newTime));
                            }
                            seminarDateTime = setToNextDay(seminarDateTime);
                            //check weekend
                            seminarDateTime = checkWeekend(seminarDateTime);
                        } else {
                            detailResponseList.add(appendSeminarDetail(seminarDateTime, line));
                            seminarDateTime = newTime;
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new Error("Seminar service: Invalid file format.");
        }
        return responseList;
    }

    boolean isMatchPattern(String line) {
        minuteMatcher = minutePattern.matcher(line);
        return minuteMatcher.find();
    }

    private String getDayFormat(LocalDateTime localDatetime, int countDay) {
        StringBuilder day = new StringBuilder();
        day.append("Day " + countDay + " " + changeThaiBuddhistFormat(localDatetime));
        return day.toString();
    }

    private SeminarDetailResponse appendSeminarDetail(LocalDateTime localDateTime, String line) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(localDateTime.format(timeFormatter));
        response.setSeminar(line);
        return response;
    }

    private SeminarDetailResponse appendLunch() {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(LocalTime.of(12, 0).format(timeFormatter));
        response.setSeminar(new String("Lunch"));
        return response;
    }

    private SeminarDetailResponse appendNetworkingEvent(LocalDateTime localDateTime) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(localDateTime.format(timeFormatter));
        response.setSeminar(new String("Networking Event"));
        return response;
    }

    boolean isLunch(LocalDateTime newTime) {
        return (newTime.getHour() >= 12 && newTime.getHour() < 13);
    }

    boolean isAfterFivePM(LocalDateTime newTime) {
        return (newTime.getHour() >= 17 && newTime.getMinute() > 0);
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

    private LocalDateTime checkWeekend(LocalDateTime localDateTime) {
        while (localDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateTime.getDayOfWeek() == DayOfWeek.SUNDAY) {
            localDateTime = localDateTime.plusDays(1);
        }
        return localDateTime;
    }

    public static String changeThaiBuddhistFormat(LocalDateTime dateTime) {
        ThaiBuddhistDate thaiBuddhistDate = ThaiBuddhistDate.from(dateTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return formatter.format(thaiBuddhistDate);
    }

}