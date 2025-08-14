package com.example.book.handler;

import com.nimbusds.jwt.proc.ExpiredJWTException;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = { LockedException.class })
    public ResponseEntity<ExceptionResponse> handleException (LockedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ErrorCodes.ACCOUNT_LOCKED.getCode())
                                .businessErrorDescription(ErrorCodes.ACCOUNT_LOCKED.getMessage())
                                .error(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(value = { DisabledException.class })
    public ResponseEntity<ExceptionResponse> handleException (DisabledException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ErrorCodes.ACCOUNT_DISABLED.getCode())
                                .businessErrorDescription(ErrorCodes.ACCOUNT_DISABLED.getMessage())
                                .error(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<ExceptionResponse> handleException (BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ErrorCodes.BAD_CREDENTIALS.getCode())
                                .businessErrorDescription(ErrorCodes.BAD_CREDENTIALS.getMessage())
                                .error(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(value = { ParseException.class, MissingRequestCookieException.class, ExpiredJWTException.class})
    public ResponseEntity<ExceptionResponse> handleException (ParseException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ErrorCodes.TOKEN_EXPIRED.getCode())
                                .businessErrorDescription(ErrorCodes.TOKEN_EXPIRED.getMessage())
                                .error(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(value = { MessagingException.class })
    public ResponseEntity<ExceptionResponse> handleException (MessagingException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .error(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<ExceptionResponse> handleException (MethodArgumentNotValidException exception) {

        Set<String> errors = new HashSet<>();
        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ExceptionResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<ExceptionResponse> handleException (Exception exception) {

        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorDescription("Internal Server Error, contact the administrator")
                                .error(exception.getMessage())
                                .build()
                );
    }
}
