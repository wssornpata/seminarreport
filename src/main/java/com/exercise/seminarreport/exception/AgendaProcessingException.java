package com.exercise.seminarreport.exception;

//@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AgendaProcessingException extends RuntimeException {
    public AgendaProcessingException(String message) {
        super(message);
    }
}