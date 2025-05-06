package com.airbnb.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

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

        /**
         * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid
         * validation.
         *
         * @param ex      the MethodArgumentNotValidException that is thrown when @Valid
         *                validation fails
         * @param headers HttpHeaders
         * @param status  HttpStatus
         * @param request WebRequest
         * @return the ErrorResponse object
         */
        @Override
        // TODO: refactor response
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex,
                        HttpHeaders headers,
                        HttpStatusCode status,
                        WebRequest request) {
                String path = ((ServletWebRequest) request).getRequest().getRequestURI();

                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining("; "));
                ErrorResponse response = ErrorResponse.builder()
                                .message(message)
                                .path(path)
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

        @ExceptionHandler(value = { BadCredentialsException.class, UsernameNotFoundException.class,
                        MissingRequestCookieException.class })
        public ResponseEntity<ErrorResponse> handleAuthException(Exception ex, HttpServletRequest httpRequest) {

                ErrorResponse response = ErrorResponse.builder()
                                .message(ex.getMessage())
                                .path(httpRequest.getRequestURI())
                                .build();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
}
