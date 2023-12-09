package com.map.gaja.client.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LocationOutsideKoreaException extends BusinessException {
    private static final String MESSAGE = "한국 내에 존재하지 않는 위치 정보입니다.";

    public LocationOutsideKoreaException() {
        super(HttpStatus.BAD_REQUEST, MESSAGE);
    }
}
