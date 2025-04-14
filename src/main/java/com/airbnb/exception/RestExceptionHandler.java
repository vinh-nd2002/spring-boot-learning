package com.airbnb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
// import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

// @RestControllerAdvice
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleException(
                        Exception ex, HttpServletRequest httpRequest) {

                ErrorResponse response = ErrorResponse.builder()
                                .message(ex.getMessage())
                                .path(httpRequest.getRequestURI())
                                .build();
                return ResponseEntity.internalServerError().body(response);
        }

        @ExceptionHandler(TestException.class)
        public ResponseEntity<ErrorResponse> handleTestException(TestException ex, HttpServletRequest httpRequest) {

                ErrorResponse response = ErrorResponse.builder()
                                .message(ex.getMessage())
                                .path(httpRequest.getRequestURI())
                                .build();
                return ResponseEntity.internalServerError().body(response);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
                        MethodArgumentTypeMismatchException ex, HttpServletRequest httpRequest) {

                ErrorResponse response = ErrorResponse.builder()
                                .message(ex.getMessage())
                                .path(httpRequest.getRequestURI())
                                .build();
                return ResponseEntity.badRequest().body(response);
        }

        @ExceptionHandler(EntityNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        EntityNotFoundException ex, HttpServletRequest httpRequest) {

                ErrorResponse response = ErrorResponse.builder()
                                .message(ex.getMessage())
                                .path(httpRequest.getRequestURI())
                                .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

}
