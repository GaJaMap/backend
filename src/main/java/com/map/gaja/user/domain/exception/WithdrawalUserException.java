package com.map.gaja.user.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class WithdrawalUserException extends BusinessException {
    private static final String MESSAGE = "회원 탈퇴를 처리하고 있는 유저입니다. (최대 하루 소요)";

    public WithdrawalUserException() {
        super(HttpStatus.GONE, MESSAGE);
    }
}
