package com.map.gaja.group.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ClientNotDeletedException extends BusinessException {
    private static final String MESSAGE = "그룹 안에 고객보다 많은 수의 고객을 삭제할 수 없습니다.";

    public ClientNotDeletedException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, MESSAGE);
    }
}
