package com.exercise.seminarreport.exception;

import com.exercise.seminarreport.dto.seminar.exception.FailureResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(QueueProducerException.class)
    public ResponseEntity<Object> handleQueueServiceInitializeException(QueueProducerException ex) {
        String msg = "Queue service initialize error " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AgendaProcessingException.class)
    public ResponseEntity<Object> handleAgendaProcessingException(AgendaProcessingException ex) {
        String msg = "Agenda processing error: " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<Object> handleInvalidDateFormatException(InvalidDateFormatException ex) {
        String msg = "Invalid date format: " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LineNullException.class)
    public ResponseEntity<Object> handleLineNullException(LineNullException ex) {
        String msg = "Line is null: " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        String msg = "I/O error: " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex) {
        String msg = "An error occurred: " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        String msg = "Illegal argument: " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex) {
        String msg = "Illegal state: " + ex.getMessage();
        logger.error(msg);
        return new ResponseEntity<>(new FailureResponse(msg), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}