package com.exercise.seminarreport.service;

import com.exercise.seminarreport.dto.seminar.response.SeminarResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public byte[] exportToPdf(List<SeminarResponse> seminarResponseList, String jasperFilename) {
        logger.info("Start export to PDF.");
        try {
            String json = objectMapper.writeValueAsString(seminarResponseList);
            return generatePdfFromJson(json, jasperFilename);
        } catch (JsonProcessingException e) {
            logger.error("Error processing JSON", e);
            throw new IllegalArgumentException("Error processing JSON", e);
        }
    }

    public byte[] exportToPdf(String jsonInput, String jasperFilename) {
        return generatePdfFromJson(jsonInput, jasperFilename);
    }

    private byte[] generatePdfFromJson(String json, String jasperFilename) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (!isValidJson(json)) {
                throw new IllegalArgumentException("Invalid JSON.");
            }
            File file = ResourceUtils.getFile("classpath:jasper-template/" + jasperFilename);
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            try (InputStream jsonInputStream = new ByteArrayInputStream(json.getBytes())) {
                JsonDataSource jsonDataSource = new JsonDataSource(jsonInputStream);
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("datasource", jsonDataSource);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jsonDataSource);
                JasperExportManager.exportReportToPdfStream(jasperPrint, out);
                return out.toByteArray();
            }
        } catch (FileNotFoundException e) {
            logger.error("Jasper template file not found", e);
            throw new IllegalStateException("Jasper template file not found: " + jasperFilename, e);
        } catch (JRException e) {
            logger.error("Jasper report generation failed", e);
            throw new IllegalStateException("Jasper report generation failed", e);
        } catch (IOException e) {
            logger.error("IO error during PDF generation", e);
            throw new IllegalStateException("IO error during PDF generation", e);
        }
    }

    public boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            logger.warn("Invalid JSON syntax", e);
            return false;
        }
    }
}