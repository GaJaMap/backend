package com.map.gaja.user.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {
    private static final String message = "사용자를 찾을 수 없습니다.";

    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, message);
    }

    public UserNotFoundException(Throwable e) {
        super(e, HttpStatus.NOT_FOUND, message);
    }
}
