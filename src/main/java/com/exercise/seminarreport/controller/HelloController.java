package com.exercise.seminarreport.controller;

import com.exercise.seminarreport.service.HelloService;
import net.sf.jasperreports.engine.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/")
public class HelloController {
    private final HelloService helloService;

    public HelloController(HelloService helloService, HelloService helloService1) {
        this.helloService = helloService1;
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello() {
        String hello = helloService.sayHello();
        return ResponseEntity.status(HttpStatus.OK).body(hello);
    }

    @GetMapping("/greet/{name}")
    public ResponseEntity<?> greet(@PathVariable String name) {
        String hello = helloService.sayHello(name);
        return ResponseEntity.status(HttpStatus.OK).body(hello);
    }

    @GetMapping(value = "/jasper", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generate() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        File file;
        try {
            file = ResourceUtils.getFile("classpath:jasper-template/hello.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(jasperPrint, out);
            return ResponseEntity.ok(out.toByteArray());
        } catch (IOException | JRException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
