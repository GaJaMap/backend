package com.map.gaja.client.domain.exception;

import org.springframework.http.HttpStatus;

public class S3NotWorkingException extends RuntimeException {
    private final String message = "서버 내부에서 오류가 발생했습니다.";
    private final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public S3NotWorkingException() {
    }

    public S3NotWorkingException(Throwable cause) {
        super(cause);
    }
}
