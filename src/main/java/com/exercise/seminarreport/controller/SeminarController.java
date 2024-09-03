package com.exercise.seminarreport.controller;

import com.exercise.seminarreport.dto.seminar.request.SeminarFileRequest;
import com.exercise.seminarreport.service.ReportService;
import com.exercise.seminarreport.service.SeminarService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/seminar")
public class SeminarController {

    private final String jasperPath = "SeminarReport.jrxml";
    private final String fileName = "Seminar.pdf";

    private final SeminarService seminarService;
    private final ReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(SeminarController.class);

    public SeminarController(SeminarService seminarService, ReportService reportService) {
        this.seminarService = seminarService;
        this.reportService = reportService;
    }

    @PostMapping(value = "/uploadFile", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@Valid @ModelAttribute SeminarFileRequest seminarFileRequest) {
        MultipartFile file = seminarFileRequest.getFile();
        var response = seminarService.getSeminarResponse(file);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(value = "/uploadFileAndExport", produces = MediaType.APPLICATION_PDF_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFileAndExport(@Valid @ModelAttribute SeminarFileRequest seminarFileRequest) {
        MultipartFile file = seminarFileRequest.getFile();
        var response = seminarService.getSeminarResponse(file);
        byte[] report = reportService.exportToPdf(response, jasperPath);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(report);
    }

    @PostMapping(value = "/exportPdf", produces = MediaType.APPLICATION_PDF_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> exportPdf(@Valid @RequestBody String jsonInput) {
        byte[] report = reportService.exportToPdf(jsonInput, jasperPath);
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.APPLICATION_PDF)
                .body(report);
    }
}