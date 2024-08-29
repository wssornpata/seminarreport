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

    private final String minRegex = "(\\d+)min";

    private final DateTimeFormatter dateTimeFormatter;
    private final Pattern minutePattern;
    private Matcher minuteMatcher;

    public SeminarService(DateService dateService) {
        this.dateService = dateService;
        this.minutePattern = Pattern.compile(minRegex);
        String datePattern = "yyyy-MM-dd";
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
            String line = null;
            int countDay = 0;
            boolean isRunning = true;

            while (isRunning) {
                if (countDay == 0) {
                    String date = reader.readLine();
                    try {
                        LocalDate parsedDate = LocalDate.parse(date, dateTimeFormatter);
                        seminarDateTime = parsedDate.atTime(9, 0);
                        countDay++;
                        log.info("Get startDate From File.");
                    } catch (DateTimeParseException e) {
                        isRunning = false;
                        throw new Error("Seminar service: Invalid date format.");
                    }
                } else {
                    SeminarResponse response = new SeminarResponse();
                    List<SeminarDetailResponse> detailResponseList = new ArrayList<>();
                    response.setDate(seminarDateTime.toLocalDate());
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = minutePattern.matcher(line);

                        if (!matcher.find()) {
                            continue;
                        }

                        if (dateService.isNineAM(seminarDateTime)) {
                            countDay++;
                        }

                        line = line.replaceAll(minRegex, "");
                        int duration = Integer.parseInt(matcher.group(1));
                        LocalDateTime newTime = seminarDateTime.plusMinutes(duration);

                        if (dateService.isLunch(newTime)) {
                            if(dateService.isEqualLunch(newTime)){
                                detailResponseList.add(appendSeminarDetail(seminarDateTime, duration, line));
                                detailResponseList.add(appendLunch());
                                seminarDateTime = dateService.setToAfternoon(seminarDateTime);
                            }else {
                                detailResponseList.add(appendLunch());
                                seminarDateTime = dateService.setToAfternoon(seminarDateTime);
                                detailResponseList.add(appendSeminarDetail(seminarDateTime, duration, line));
                                seminarDateTime = seminarDateTime.plusMinutes(duration);
                            }
                        } else if (dateService.isNetworkingEvent(newTime)) {
                            LocalDateTime time = newTime;
                            if (dateService.isAfterFivePM(newTime)) {
                                time = seminarDateTime;
                            } else {
                                detailResponseList.add(appendSeminarDetail(seminarDateTime, duration, line));
                            }
                            detailResponseList.add(appendNetworkingEvent(time));
                            seminarDateTime = dateService.setToNextDay(seminarDateTime);
                            seminarDateTime = dateService.checkWeekend(seminarDateTime);
                            break;
                        } else {
                            detailResponseList.add(appendSeminarDetail(seminarDateTime, duration, line));
                            seminarDateTime = newTime;
                        }
                    }

                    if(line == null) {
                        if (dateService.checkLastNetworkingEvent(seminarDateTime)) {
                            detailResponseList.add(appendNetworkingEvent(seminarDateTime));
                        }
                        isRunning = false;
                    }

                    if(!detailResponseList.isEmpty()) {
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