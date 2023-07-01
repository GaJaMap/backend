package com.map.gaja.client.domain.exception;

import org.springframework.http.HttpStatus;

public class ClientNotInGroupException extends RuntimeException {
    private String message = "그룹 내에 사용자를 찾을 수 없습니다.";
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public ClientNotInGroupException() {
    }

    public ClientNotInGroupException(Throwable cause) {
        super(cause);
    }
}
