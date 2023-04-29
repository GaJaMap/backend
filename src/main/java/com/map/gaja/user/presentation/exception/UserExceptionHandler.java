package com.map.gaja.user.presentation.exception;

import com.map.gaja.user.domain.exception.UserNotFoundException;
import com.map.gaja.user.presentation.dto.response.NotFoundResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice(basePackages = "com.map.gaja.user.presentation.api")
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<NotFoundResponse> handleUserNotFound(UserNotFoundException e) {
        return new ResponseEntity<>(
                new NotFoundResponse(e.getMessage()), e.getStatus()
        );
    }
}
