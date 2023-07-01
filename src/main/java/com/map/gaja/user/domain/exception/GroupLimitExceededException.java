package com.map.gaja.user.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class GroupLimitExceededException extends BusinessException {

    public GroupLimitExceededException(String authority, Integer groupLimitCount) {
        super(HttpStatus.FORBIDDEN, String.format("회원님의 등급은 %s로 최대 %d개의 그룹만 생성 가능합니다.", authority, groupLimitCount));
    }
}
