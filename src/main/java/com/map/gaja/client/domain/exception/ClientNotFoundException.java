package com.map.gaja.client.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ClientNotFoundException extends BusinessException {
    public ClientNotFoundException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "존재하지 않은 클라이언트입니다.");
    }
}
