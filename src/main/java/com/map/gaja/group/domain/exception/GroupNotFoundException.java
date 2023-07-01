package com.map.gaja.group.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class GroupNotFoundException extends RuntimeException{
    private final String message = "존재하지 않은 그룹이거나 사용자의 그룹이 아닙니다.";
    private final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
}
