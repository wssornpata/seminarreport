package com.exercise.seminarreport.service;

import com.exercise.seminarreport.dto.seminar.response.SeminarDetailResponse;
import com.exercise.seminarreport.dto.seminar.response.SeminarResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SeminarService {

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
        log.info("Get File.");

        List<SeminarResponse> responseList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))){
            log.info("Read File.");
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
                        log.info("Get Start Seminar Date From File.");
                    } catch (DateTimeParseException e) {
                        throw new Error("Seminar service: Invalid date format.");
                    }
                }else {
                    if (isMatchPattern(line))  {
                        if (dateService.isNineAM(seminarDateTime)) {
                            response.setDate(seminarDateTime.toLocalDate().toString());
                            response.setAgendas(detailResponseList);
                            responseList.add(response);
                            log.info("Write "+response.getDate().toString()+" "+ seminarDateTime.getDayOfWeek());
                            response = new SeminarResponse();
                            if(countDay > 1) {
                                detailResponseList = new ArrayList<>();
                            }
                            countDay++;
                        }

                        line = line.trim();
                        int minute = Integer.parseInt(minuteMatcher.group(1));
                        LocalDateTime newTime = seminarDateTime.plusMinutes(minute);

                        if (dateService.isLunch(newTime)) {
                            detailResponseList.add(appendSeminarDetail(seminarDateTime, line));
                            detailResponseList.add(appendLunch());
                            seminarDateTime = dateService.setToAfternoon(seminarDateTime);
                        } else if (dateService.isNetworkingEvent(newTime)) {
                            if (dateService.isAfterFivePM(newTime)) {
                                detailResponseList.add(appendNetworkingEvent(seminarDateTime));
                            } else {
                                detailResponseList.add(appendSeminarDetail(seminarDateTime, line));
                                detailResponseList.add(appendNetworkingEvent(newTime));
                            }
                            seminarDateTime = dateService.setToNextDay(seminarDateTime);
                            //check weekend
                            seminarDateTime = dateService.checkWeekend(seminarDateTime);
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

    public boolean isMatchPattern(String line) {
        minuteMatcher = minutePattern.matcher(line);
        return minuteMatcher.find();
    }

    private String getDayFormat(LocalDateTime localDatetime, int countDay) {
        StringBuilder day = new StringBuilder();
        day.append("Day " + countDay + " " + dateService.changeThaiBuddhistFormat(localDatetime));
        return day.toString();
    }

    private SeminarDetailResponse appendSeminarDetail(LocalDateTime localDateTime, String line) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTimeDuration(localDateTime.toLocalTime().format(timeFormatter).toString());
        response.setSeminar(line);
        return response;
    }

    private SeminarDetailResponse appendLunch() {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTimeDuration(LocalTime.of(12, 0).format(timeFormatter).toString());
        response.setSeminar(new String("Lunch"));
        return response;
    }

    private SeminarDetailResponse appendNetworkingEvent(LocalDateTime localDateTime) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTimeDuration(localDateTime.toLocalTime().format(timeFormatter).toString());
        response.setSeminar(new String("Networking Event"));
        return response;
    }
}