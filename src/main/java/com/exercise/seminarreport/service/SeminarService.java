package com.exercise.seminarreport.service;

import com.exercise.seminarreport.dto.seminar.response.SeminarDetailResponse;
import com.exercise.seminarreport.dto.seminar.response.SeminarResponse;
import com.exercise.seminarreport.entity.AgendaEntity;
import com.exercise.seminarreport.exception.LineNullException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeminarService {

    private static final Logger logger = LoggerFactory.getLogger(SeminarService.class);

    public List<SeminarResponse> getSeminarResponse(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalStateException("File is empty");
        }
        logger.info("Received File.");

        List<SeminarResponse> responseList = new ArrayList<>();
        try (QueueProducer queueProducer = new QueueProducer(file)) {
            LocalDateTime startDateTime = queueProducer.getLocalDateTime();
            AgendaEntity agendaEntity = null;

            boolean isRunning = true;
            while (isRunning) {
                SeminarResponse response = new SeminarResponse();
                List<SeminarDetailResponse> detailResponseList = new ArrayList<>();

                response.setDate(startDateTime.toLocalDate());

                while ((agendaEntity = queueProducer.consume()) != null) {
                    try {
                        String agendaName = agendaEntity.getAgendaName();
                        int agendaDuration = agendaEntity.getAgendaDuration();
                        LocalDateTime endDateTime = startDateTime.plusMinutes(agendaDuration);

                        if (TimeService.isLunch(endDateTime)) {
                            if (TimeService.isEqualLunch(endDateTime)) {
                                detailResponseList.add(appendSeminarDetail(startDateTime, agendaDuration, agendaName));
                                detailResponseList.add(appendLunch());
                                startDateTime = TimeService.setToAfternoon(startDateTime);
                            } else {
                                detailResponseList.add(appendLunch());
                                startDateTime = TimeService.setToAfternoon(startDateTime);
                                detailResponseList.add(appendSeminarDetail(startDateTime, agendaDuration, agendaName));
                                startDateTime = startDateTime.plusMinutes(agendaDuration);
                            }
                        } else if (TimeService.isNetworkingEvent(endDateTime)) {
                            LocalDateTime time = endDateTime;
                            if (TimeService.isAfterFivePM(endDateTime)) {
                                time = startDateTime;
                            } else {
                                detailResponseList.add(appendSeminarDetail(startDateTime, agendaDuration, agendaName));
                            }
                            detailResponseList.add(appendNetworkingEvent(time));
                            startDateTime = TimeService.setToNextDay(startDateTime);
                            startDateTime = TimeService.checkWeekend(startDateTime);
                            break;
                        } else {
                            detailResponseList.add(appendSeminarDetail(startDateTime, agendaDuration, agendaName));
                            startDateTime = endDateTime;
                        }
                    } catch (NullPointerException e) {
                        throw new LineNullException("Error processing agenda entity");
                    }
                }

                if (queueProducer.isLastline()) {
                    if (TimeService.checkLastNetworkingEvent(startDateTime)) {
                        detailResponseList.add(appendNetworkingEvent(startDateTime));
                    }
                    isRunning = false;
                }

                if (!detailResponseList.isEmpty()) {
                    response.setAgendas(detailResponseList);
                    responseList.add(response);
                    logger.info("Add\t{}", response.getDate());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred", e);
        }
        return responseList;
    }

    private SeminarDetailResponse appendSeminarDetail(LocalDateTime localDateTime, int minute, String line) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(localDateTime.toLocalTime());
        response.setSeminar(line);
        response.setDuration(String.valueOf(minute));
        return response;
    }

    private SeminarDetailResponse appendLunch() {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(LocalTime.of(12, 0));
        response.setSeminar("Lunch");
        response.setDuration(String.valueOf(60));
        return response;
    }

    private SeminarDetailResponse appendNetworkingEvent(LocalDateTime localDateTime) {
        SeminarDetailResponse response = new SeminarDetailResponse();
        response.setTime(localDateTime.toLocalTime());
        response.setSeminar("Networking Event");
        response.setDuration(String.valueOf(60));
        return response;
    }
}