package com.map.gaja.user.domain.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
public class UserNotFoundException extends RuntimeException{
    private final String message = "사용자를 찾을 수 없습니다.";
    private final HttpStatus status = HttpStatus.NOT_FOUND;
}
