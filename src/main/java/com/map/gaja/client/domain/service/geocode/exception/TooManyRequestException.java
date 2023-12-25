package com.map.gaja.client.domain.service.geocode.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TooManyRequestException extends BusinessException {
    private static final String MESSAGE = "많은 요청으로 인해 잠시 후 이용 부탁드립니다.";

    public TooManyRequestException() {
        super(HttpStatus.TOO_MANY_REQUESTS, MESSAGE);
    }
}
