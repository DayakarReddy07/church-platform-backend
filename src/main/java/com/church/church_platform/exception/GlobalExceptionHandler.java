package com.church.church_platform.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle our custom RuntimeExceptions
    // (email exists, user not found, etc.)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    //  Handle validation errors (@NotBlank, @Email etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", 400);
        error.put("error", "Validation Failed");

        // Collect all validation error messages
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.put(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                )
        );
        error.put("messages", fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    //  Handle bad credentials (wrong password)
    @ExceptionHandler(
            org.springframework.security.authentication
                    .BadCredentialsException.class
    )
    public ResponseEntity<Map<String, Object>> handleBadCredentials(
            Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", 401);
        error.put("error", "Unauthorized");
        error.put("message", "Invalid email or password!");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    // Handle user not found
    @ExceptionHandler(
            org.springframework.security.core.userdetails
                    .UsernameNotFoundException.class
    )
    public ResponseEntity<Map<String, Object>> handleUserNotFound(
            Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", 404);
        error.put("error", "Not Found");
        error.put("message", "User not found!");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    // Handle all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(
            Exception ex) {

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}