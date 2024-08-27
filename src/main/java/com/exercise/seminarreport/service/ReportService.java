package com.exercise.seminarreport.service;

import com.exercise.seminarreport.dto.seminar.response.SeminarResponse;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JsonDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ReportService {

    public byte[] exportToPdf(List<SeminarResponse> seminarResponseList, String filename) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Gson gson = new Gson();
        File file;
        try {
            file = ResourceUtils.getFile("classpath:jasper-template/"+filename);
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            String json = gson.toJson(seminarResponseList);
            InputStream jsonInputStream = new ByteArrayInputStream(json.getBytes());
            JsonDataSource jsonDataSource = new JsonDataSource(jsonInputStream);
            Map dataSource = new HashMap<>();
            dataSource.put("datasource",jsonDataSource);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dataSource, jsonDataSource);
            log.info(jsonDataSource.toString());
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            return out.toByteArray();
        } catch (FileNotFoundException | JRException e) {
            throw new Error("Report generation failed: " + e.getMessage());
        }
    }
}