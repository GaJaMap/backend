package com.map.gaja.client.infrastructure.location.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TooManyRequestException extends BusinessException {
    private static final String MESSAGE = "현재 엑셀 업로드 서비스를 사용할 수 없습니다.";

    public TooManyRequestException() {
        super(HttpStatus.TOO_MANY_REQUESTS, MESSAGE);
    }
}
