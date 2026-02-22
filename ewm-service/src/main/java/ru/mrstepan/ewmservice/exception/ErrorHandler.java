package ru.mrstepan.ewmservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        log.error("NotFoundException: {}", e.getMessage());
        return new ApiError(List.of(), e.getMessage(), "The required object was not found.", "NOT_FOUND", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        log.error("ConflictException: {}", e.getMessage());
        return new ApiError(List.of(), e.getMessage(), "For the requested operation the conditions are not met.", "CONFLICT", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(BadRequestException e) {
        log.error("BadRequestException: {}", e.getMessage());
        return new ApiError(List.of(), e.getMessage(), "Incorrectly made request.", "BAD_REQUEST", LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(MethodArgumentNotValidException e) {
        log.error("ValidationException: {}", e.getMessage());
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> "Field: " + fe.getField() + ". Error: " + fe.getDefaultMessage() + ". Value: " + fe.getRejectedValue())
                .collect(Collectors.toList());
        return new ApiError(errors, errors.isEmpty() ? e.getMessage() : errors.get(0), "Incorrectly made request.", "BAD_REQUEST", LocalDateTime.now());
    }
}
