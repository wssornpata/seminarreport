package com.exercise.seminarreport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LineNullException extends RuntimeException {
    public LineNullException(String message) {
        super(message);
    }
}
