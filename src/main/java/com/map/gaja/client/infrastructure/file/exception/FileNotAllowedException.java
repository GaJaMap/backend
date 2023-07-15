package com.map.gaja.client.infrastructure.file.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class FileNotAllowedException extends BusinessException {

    public FileNotAllowedException(Throwable cause) {
        super(cause, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 형식이거나, 잘못된 파일입니다.");
    }

    public FileNotAllowedException() {
        super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 형식이거나, 잘못된 파일입니다.");
    }
}
