package com.map.gaja.client.domain.exception;

import org.springframework.http.HttpStatus;

public class InvalidFileException extends RuntimeException {
    private final String message = "잘못된 파일 형식입니다.";
    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidFileException() {
    }

    public InvalidFileException(Throwable cause) {
        super(cause);
    }
}
