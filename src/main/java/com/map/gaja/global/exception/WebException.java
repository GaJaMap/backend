package com.map.gaja.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class WebException extends RuntimeException {
    private final HttpStatus status;
    private final Object body;

    public WebException(Throwable cause, HttpStatus status, Object body) {
        super(cause);
        this.status = status;
        this.body = body;
    }

    public WebException(HttpStatus status, Object body) {
        this.status = status;
        this.body = body;
    }
}
