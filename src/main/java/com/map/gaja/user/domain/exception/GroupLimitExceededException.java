package com.map.gaja.user.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GroupLimitExceededException extends RuntimeException {
    private String message;
    private final HttpStatus status = HttpStatus.FORBIDDEN;

    public GroupLimitExceededException(String authority, Integer groupLimitCount) {
        this.message = String.format("회원님의 등급은 %s로 최대 %d개의 그룹만 생성 가능합니다.", authority, groupLimitCount);
    }
}
