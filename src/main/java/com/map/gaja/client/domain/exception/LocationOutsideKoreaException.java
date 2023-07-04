package com.map.gaja.client.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LocationOutsideKoreaException extends BusinessException {
    public LocationOutsideKoreaException() {
        super(HttpStatus.FORBIDDEN, "한국 내에 존재하지 않는 위치 정보입니다.");
    }
}
