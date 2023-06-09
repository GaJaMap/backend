package com.map.gaja.client.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class InvalidFileException extends BusinessException {
    public InvalidFileException() {
        super(HttpStatus.BAD_REQUEST, "잘못된 파일입니다.");
    }

    public InvalidFileException(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST, "잘못된 파일입니다.");
    }
}
