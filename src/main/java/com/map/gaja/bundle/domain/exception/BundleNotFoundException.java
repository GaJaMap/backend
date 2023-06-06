package com.map.gaja.bundle.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class BundleNotFoundException extends RuntimeException{
    private final String message = "존재하지 않은 번들이거나 사용자의 번들이 아닙니다.";
    private final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
}
