package com.exercise.seminarreport.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    public byte[] exportToPdf(List<?> seminarResponseList, String filename) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        File file;
        try {
            file = ResourceUtils.getFile("classpath:jasper-template/"+filename);
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JRBeanCollectionDataSource seminarBeanDataSource = new JRBeanCollectionDataSource(seminarResponseList);
            Map dataSource = new HashMap<>();
            dataSource.put("datasource",seminarBeanDataSource);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dataSource, seminarBeanDataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            return out.toByteArray();
        } catch (FileNotFoundException | JRException e) {
            throw new Error("Report generation failed: " + e.getMessage());
        }
    }
}