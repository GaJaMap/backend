package com.map.gaja.client.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidFileException extends BusinessException {
    private static final String MESSAGE = "잘못된 파일입니다.";

    public InvalidFileException() {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, MESSAGE);
    }

    public InvalidFileException(Throwable cause) {
        super(cause, HttpStatus.UNSUPPORTED_MEDIA_TYPE, MESSAGE);
    }
}
