package com.map.gaja.client.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UnsupportedFileTypeException extends BusinessException {
    public UnsupportedFileTypeException(String exceptionFileType) {
        super(HttpStatus.BAD_REQUEST, String.format("%s는 지원하지 않는 파일 형식입니다.", exceptionFileType));
    }
}
