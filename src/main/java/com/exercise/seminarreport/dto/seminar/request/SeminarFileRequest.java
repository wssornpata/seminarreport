package com.exercise.seminarreport.dto.seminar.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SeminarFileRequest {
    private MultipartFile file;
}
