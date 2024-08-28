package com.exercise.seminarreport.controller;

import com.exercise.seminarreport.service.ReportService;
import com.exercise.seminarreport.service.SeminarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/seminar")
public class SeminarController {

    private final SeminarService seminarService;
    private final ReportService reportService;

    public SeminarController(SeminarService seminarService, ReportService reportService) {
        this.seminarService = seminarService;
        this.reportService = reportService;
    }

    @PostMapping(value = "/upload", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            var response = seminarService.getSeminarResponse(file);
            log.info("Write succeeded");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Error e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value = "/uploadFileAndExport", produces = "application/json", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> exportToPdf(@RequestParam("file") MultipartFile file) {
        try {
            var response = seminarService.getSeminarResponse(file);
            byte[] report = reportService.exportToPdf(response, "SeminarReport.jrxml");
            log.info("Write succeeded");
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Seminar.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(report);
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping(value = "/exportPdf", produces = "application/json")
    public ResponseEntity<?> exportToPdf(@RequestBody() String jsonInput) {
        try {
            byte[] report = reportService.exportToPdf(jsonInput, "SeminarReport.jrxml");
            log.info("Write succeeded");
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Seminar.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(report);
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
