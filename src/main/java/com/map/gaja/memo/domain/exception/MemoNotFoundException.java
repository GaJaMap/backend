package com.map.gaja.memo.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class MemoNotFoundException extends BusinessException {
    private static final String MESSAGE = "존재하지 않는 메모입니다.";

    public MemoNotFoundException() {
        super(HttpStatus.NOT_FOUND, MESSAGE);
    }
}

