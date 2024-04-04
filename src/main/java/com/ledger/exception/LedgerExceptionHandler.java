package com.ledger.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LedgerExceptionHandler {

    @ExceptionHandler(value = {LedgerException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleException(LedgerException exception) {
        return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), exception.getMessage()) {
        };
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage globalHandleException(EntityNotFoundException exception) {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage()) {
        };
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage globalHandleException(Exception exception) {
        return new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()) {
        };
    }

}