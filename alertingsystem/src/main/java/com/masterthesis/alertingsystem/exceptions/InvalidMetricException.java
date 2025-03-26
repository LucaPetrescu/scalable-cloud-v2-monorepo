package com.masterthesis.alertingsystem.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class InvalidMetricException extends Throwable {

    public InvalidMetricException(String message){ super(message); }

}
