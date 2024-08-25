package com.exercise.seminarreport.controller;

import com.exercise.seminarreport.service.ReportService;
import com.exercise.seminarreport.service.SeminarService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
//            byte[] report = reportService.generateReport(response);
//            return ResponseEntity.status(HttpStatus.OK)
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(report);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Error e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
