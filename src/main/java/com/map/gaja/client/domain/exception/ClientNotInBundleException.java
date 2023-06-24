package com.map.gaja.client.domain.exception;

import org.springframework.http.HttpStatus;

public class ClientNotInBundleException extends RuntimeException {
    private String message = "번들 내에 사용자를 찾을 수 없습니다.";
    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public ClientNotInBundleException() {
    }

    public ClientNotInBundleException(Throwable cause) {
        super(cause);
    }
}
