package com.exercise.seminarreport.service;

import com.exercise.seminarreport.dto.seminar.response.SeminarResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
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
    final TypeAdapter<JsonElement> strictAdapter = new Gson().getAdapter(JsonElement.class);

    public byte[] exportToPdf(List<SeminarResponse> seminarResponseList, String jasperFilename) {
        Gson gson = new Gson();//ObjectMapper
        String json = gson.toJson(seminarResponseList);
        return getPdf(json, jasperFilename);
    }

    public byte[] exportToPdf(String jsonInput, String jasperFilename) {
        return getPdf(jsonInput, jasperFilename);
    }

    private byte[] getPdf(String json, String jasperFilename) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        File file;
        try {
            if(!isValid(json)){
                throw new Error("Invalid JSON.");
            }
            file = ResourceUtils.getFile("classpath:jasper-template/"+jasperFilename);
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            InputStream jsonInputStream = new ByteArrayInputStream(json.getBytes());
            JsonDataSource jsonDataSource = new JsonDataSource(jsonInputStream);
            Map<String, Object> dataSource = new HashMap<>();
            dataSource.put("datasource",jsonDataSource);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dataSource, jsonDataSource);
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            return out.toByteArray();
        } catch (FileNotFoundException | JRException e) {
            throw new Error("Report generation failed: " + e.getMessage());
        }
    }

    public boolean isValid(String json) {
        try {
            strictAdapter.fromJson(json);
        } catch (JsonSyntaxException | IOException e) {
            return false;
        }
        return true;
    }
}