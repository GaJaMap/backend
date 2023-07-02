package com.map.gaja.group.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;


public class GroupNotFoundException extends BusinessException {

    public GroupNotFoundException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "존재하지 않은 그룹이거나 사용자의 그룹이 아닙니다.");
    }
}
