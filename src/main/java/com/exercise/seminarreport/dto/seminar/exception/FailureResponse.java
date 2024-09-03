package com.exercise.seminarreport.dto.seminar.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FailureResponse {
    private String message;
}