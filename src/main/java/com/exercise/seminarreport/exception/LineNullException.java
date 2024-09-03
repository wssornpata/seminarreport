package com.exercise.seminarreport.exception;

//@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LineNullException extends RuntimeException {
    public LineNullException(String message) {
        super(message);
    }
}
