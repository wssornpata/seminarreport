package com.exercise.seminarreport.service;

import com.exercise.seminarreport.entity.AgendaEntity;
import com.exercise.seminarreport.hadler.AgendaProcessingException;
import com.exercise.seminarreport.hadler.InvalidDateFormatException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueueProducer implements AutoCloseable {

    private static final Logger logger = LoggerFactory.getLogger(QueueProducer.class);

    private final DateTimeFormatter dateTimeFormatter;
    private final BufferedReader reader;
    private final Pattern minutePattern;
    private final String minRegex = "(\\d+)min";

    @Getter
    private final Queue<AgendaEntity> agendaQueue = new LinkedList<>();

    @Getter
    private LocalDateTime localDateTime;

    public QueueProducer(MultipartFile multipartFile) {
        try {
            this.reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
            this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            this.minutePattern = Pattern.compile(minRegex);
            this.processDate();
            this.processAgendaQueue();
        } catch (IOException e) {
            logger.error("Error initializing QueueProducer: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void processAgendaQueue() {
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                Matcher matcher = minutePattern.matcher(line);
                if (!matcher.find()) {
                    continue;
                }
                String agendaName = line.replaceAll(minRegex, "").trim();
                int timeDuration = Integer.parseInt(matcher.group(1));
                agendaQueue.add(new AgendaEntity(agendaName, timeDuration));
            }
        } catch (IOException e) {
            logger.error("Error processing agenda queue: {}", e.getMessage());
            throw new AgendaProcessingException("Error processing agenda queue");
        }
    }

    private void processDate() {
        try {
            String dateInput = reader.readLine();
            if (dateInput != null) {
                localDateTime = LocalDate.parse(dateInput, dateTimeFormatter).atTime(9, 0);
            } else {
                logger.error("Date input is null");
                throw new RuntimeException("Date input is null");
            }
        } catch (IOException e) {
            logger.error("Error reading date from file: {}", e.getMessage());
            throw new RuntimeException("Error reading date from file", e);
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date: {}", e.getMessage());
            throw new InvalidDateFormatException("Invalid date format");
        }
    }

    public AgendaEntity consume() {
        return this.agendaQueue.poll();
    }

    public boolean isLastline() {
        return this.agendaQueue.isEmpty();
    }

    @Override
    public void close() throws Exception {
        try {
            reader.close();
        } catch (IOException e) {
            logger.error("Error closing reader: {}", e.getMessage());
            throw e;
        }
    }
}