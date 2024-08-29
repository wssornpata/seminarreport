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
    private boolean isRunning = true;

    private final DateTimeFormatter dateTimeFormatter;
    private final Pattern minutePattern;
    private Matcher minuteMatcher;

    public SeminarService(DateService dateService) {
        this.dateService = dateService;
        this.minutePattern = Pattern.compile(minRegex);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
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
            SeminarResponse response;
            List<SeminarDetailResponse> detailResponseList;
            String line;
            int countDay = 0;
            isRunning = true;
            while (isRunning) {
                if (countDay == 0) {
                    line = reader.readLine();
                    String date = line;
                    try {
                        LocalDate parsedDate = LocalDate.parse(date, dateTimeFormatter);
                        seminarDateTime = parsedDate.atTime(9, 0);
                        countDay++;
                        log.info("Get Start Seminar Date From File.");
                    } catch (DateTimeParseException e) {
                        isRunning = false;
                        throw new Error("Seminar service: Invalid date format.");
                    }
                } else {
                    response = new SeminarResponse();
                    detailResponseList = new ArrayList<>();
                    response.setDate(seminarDateTime.toLocalDate().toString());
                    int minute = 0;
                    while ((line = reader.readLine()) != null) {
                        if (isMatchPattern(line)) {
                            if (dateService.isNineAM(seminarDateTime)) {
                                countDay++;
                            }
                        }
                        line = line.trim();
                        minute = Integer.parseInt(minuteMatcher.group(1));
                        line = line.replaceAll(minRegex, "");
                        LocalDateTime newTime = seminarDateTime.plusMinutes(minute);

                        if (dateService.isLunch(newTime)) {
                            detailResponseList.add(appendSeminarDetail(seminarDateTime, minute, line));
                            detailResponseList.add(appendLunch());
                            seminarDateTime = dateService.setToAfternoon(seminarDateTime);
                        } else if (dateService.isNetworkingEvent(newTime)) {
                            if (dateService.isAfterFivePM(newTime)) {
                                detailResponseList.add(appendNetworkingEvent(seminarDateTime));
                            } else {
                                detailResponseList.add(appendSeminarDetail(seminarDateTime, minute, line));
                                detailResponseList.add(appendNetworkingEvent(newTime));
                            }
                            seminarDateTime = dateService.setToNextDay(seminarDateTime);
                            seminarDateTime = dateService.checkWeekend(seminarDateTime);
                            break;
                        } else {
                            detailResponseList.add(appendSeminarDetail(seminarDateTime, minute, line));
                            seminarDateTime = newTime;
                        }
                    }

                    if(line == null) {
                        if (dateService.checkLastNetworkingEvent(seminarDateTime)) {
                            detailResponseList.add(appendNetworkingEvent(seminarDateTime));
                        }
                        isRunning = false;
                    }

                    if(detailResponseList.size()>0) {
                        response.setAgendas(detailResponseList);
                        responseList.add(response);
                        log.info("Add\t"+response.getDate());
                    }
                }
            }
        } catch (IOException e) {
            throw new Error("Seminar service: Invalid file format.");
        }
        return responseList;
    }

    public boolean isMatchPattern(String line) {
        if(line != null) {
            minuteMatcher = minutePattern.matcher(line);
            return minuteMatcher.find();
        }else {
            return false;
        }
    }

    private SeminarDetailResponse appendSeminarDetail(LocalDateTime localDateTime, int minute, String line) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(localDateTime.toLocalTime().toString());
        response.setSeminar(line);
        response.setDuration(String.valueOf(minute));
        return response;
    }

    private SeminarDetailResponse appendLunch() {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(LocalTime.of(12, 0).toString());
        response.setSeminar(new String("Lunch"));
        response.setDuration(String.valueOf(60));
        return response;
    }

    private SeminarDetailResponse appendNetworkingEvent(LocalDateTime localDateTime) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(localDateTime.toLocalTime().toString());
        response.setSeminar(new String("Networking Event"));
        response.setDuration(String.valueOf(60));
        return response;
    }
}