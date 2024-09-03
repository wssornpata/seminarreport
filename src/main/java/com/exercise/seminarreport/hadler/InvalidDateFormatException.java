package com.exercise.seminarreport.hadler;

import java.time.format.DateTimeParseException;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}

