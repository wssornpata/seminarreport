package com.exercise.seminarreport.service;

import com.exercise.seminarreport.entity.AgendaEntity;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueueProducer implements AutoCloseable{

    private final DateTimeFormatter dateTimeFormatter;
    private final BufferedReader reader;
    private final Pattern minutePattern;
    private final String minRegex = "(\\d+)min";

    @Getter
    private Queue<AgendaEntity> agendaQueue = new LinkedList<>();

    @Getter
    private LocalDateTime localDateTime;

    public QueueProducer(MultipartFile multipartFile) throws IOException {
        this.reader = new BufferedReader(new InputStreamReader(multipartFile.getInputStream()));
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.processDate();
        this.minutePattern = Pattern.compile(minRegex);
        this.processAgendaQueue();
    }

    private void processAgendaQueue() throws IOException {
        String line = null;
        while((line = reader.readLine())!=null){
            Matcher matcher = minutePattern.matcher(line);
            if (!matcher.find()) {
                continue;
            }
            String agendaName = line.replaceAll(minRegex, "");
            int timeDuration = Integer.parseInt(matcher.group(1));
            agendaQueue.add(new AgendaEntity(agendaName, timeDuration));
        }

    }

    private void processDate(){
        try {
            String dateInput = reader.readLine();
            localDateTime = LocalDate.parse(dateInput, dateTimeFormatter).atTime(9, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AgendaEntity consume(){
        return this.agendaQueue.poll();
    }

    public boolean isLastline(){
        return this.agendaQueue.isEmpty();
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
