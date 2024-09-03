package com.exercise.seminarreport.exception;

//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}