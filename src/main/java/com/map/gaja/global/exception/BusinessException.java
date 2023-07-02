package com.map.gaja.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public BusinessException(Throwable cause, HttpStatus status, String message) {
        super(cause);
        this.status = status;
        this.message = message;
    }

    public BusinessException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
