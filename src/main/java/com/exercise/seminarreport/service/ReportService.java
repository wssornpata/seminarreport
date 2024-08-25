package com.exercise.seminarreport.service;

import com.exercise.seminarreport.dto.seminar.response.SeminarResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    public byte[] generateReport(List<SeminarResponse> days) {
        // Load the JRXML file
        try{
            JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("hello.jrxml"));

            // Create a data source from the list of days
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(days);

            // Parameters for the report
            Map<String, Object> parameters = Map.of("ReportTitle", "Seminar Schedule");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException e){
            throw new Error("Report generation failed");
        }
    }
}