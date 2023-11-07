package com.map.gaja.global.location.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class TooManyRequestException extends BusinessException {

    public TooManyRequestException() {
        super(HttpStatus.TOO_MANY_REQUESTS, "너무 많은 요청으로 잠시 후 이용 부탁드립니다.");
    }
}
