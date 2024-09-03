package com.exercise.seminarreport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AgendaProcessingException extends RuntimeException {
    public AgendaProcessingException(String message) {
        super(message);
    }
}