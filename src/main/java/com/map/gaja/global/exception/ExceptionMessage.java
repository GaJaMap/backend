package com.map.gaja.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExceptionMessage {
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String description;

}
