package com.masterthesis.alertingsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidMetricException.class)
    public final ResponseEntity<Object> handleInvalidMetricException(InvalidMetricException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NothingToUpdateException.class)
    public final ResponseEntity<Object> handleNothingToUpdateException(NothingToUpdateException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<Object>(errorDetails, HttpStatus.NOT_MODIFIED);
    }

    @ExceptionHandler(ThresholdsLoadingException.class)
    public final ResponseEntity<Object> handleLoadingException(ThresholdsLoadingException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
