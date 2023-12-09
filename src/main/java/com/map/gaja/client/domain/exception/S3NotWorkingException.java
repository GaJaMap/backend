package com.map.gaja.client.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class S3NotWorkingException extends BusinessException {
    private static final String MESSAGE = "서버 내부에서 오류가 발생했습니다.";

    public S3NotWorkingException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }

    public S3NotWorkingException(Throwable cause) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR, MESSAGE);
    }
}
