package com.example.splitly.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


import java.util.Date;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ----------------------------------------
    // VALIDATION ERRORS
    // ----------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(MethodArgumentNotValidException e, WebRequest request) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .findFirst()
                .orElse(e.getMessage());

        return build(BAD_REQUEST, "Invalid Payload", message, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        String message = e.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage())
                .findFirst()
                .orElse(e.getMessage());

        return build(BAD_REQUEST, "Invalid Parameter", message, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMissingParam(MissingServletRequestParameterException e, WebRequest request) {
        return build(BAD_REQUEST, "Missing Parameter", e.getMessage(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleJsonParseError(HttpMessageNotReadableException e, WebRequest request) {
        String message = "Malformed JSON request";
        if (e.getCause() instanceof InvalidFormatException ife) {
            message = "Invalid value for field: " + ife.getPath().get(0).getFieldName();
        }
        return build(BAD_REQUEST, "Invalid JSON", message, request);
    }

    // ----------------------------------------
    // RESOURCE ERRORS
    // ----------------------------------------

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException e, WebRequest request) {
        return build(NOT_FOUND, "Not Found", e.getMessage(), request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleEntityNotFound(EntityNotFoundException e, WebRequest request) {
        return build(NOT_FOUND, "Not Found", e.getMessage(), request);
    }

    // ----------------------------------------
    // DATA CONFLICT / CONSTRAINT
    // ----------------------------------------

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleDataIntegrity(DataIntegrityViolationException e, WebRequest request) {
        return build(CONFLICT, "Conflict", "Database constraint violated", request);
    }

    @ExceptionHandler(InvalidDataException.class)
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleInvalidData(InvalidDataException e, WebRequest request) {
        return build(CONFLICT, "Conflict", e.getMessage(), request);
    }

    // ----------------------------------------
    // TYPE MISMATCH
    // ----------------------------------------

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(Exception e, WebRequest request) {
        return build(BAD_REQUEST, "Invalid Parameter Type", e.getMessage(), request);
    }

    // ----------------------------------------
    // ACCESS DENIED (if using Spring Security)
    // ----------------------------------------

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse handleAccessDenied(Exception e, WebRequest request) {
        return build(FORBIDDEN, "Permission Denied", e.getMessage(), request);
    }

    // ----------------------------------------
    // FALLBACK 500
    // ----------------------------------------

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception e, WebRequest request) {
        return build(INTERNAL_SERVER_ERROR, "Internal Server Error", e.getMessage(), request);
    }

    // ----------------------------------------
    // HELPER
    // ----------------------------------------

    private ErrorResponse build(HttpStatus status, String error, String message, WebRequest request) {
        ErrorResponse response = new ErrorResponse();
        response.setTimestamp(new Date());
        response.setStatus(status.value());
        response.setError(error);
        response.setMessage(message);
        response.setPath(request.getDescription(false).replace("uri=", ""));
        return response;
    }
}


