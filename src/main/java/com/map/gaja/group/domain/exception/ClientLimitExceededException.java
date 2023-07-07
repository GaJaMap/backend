package com.map.gaja.group.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ClientLimitExceededException extends BusinessException {
    public ClientLimitExceededException(String authority, Integer clientLimitCount) {
        super(HttpStatus.FORBIDDEN, String.format("회원님의 등급은 %s로 최대 %d명의 고객만 생성 가능합니다.", authority, clientLimitCount));
    }
}
