package com.map.gaja.client.infrastructure.geocode.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class LockAcquisitionFailedException extends BusinessException {
    private static final String MESSAGE = "lock을 획득할 수 없습니다.";

    public LockAcquisitionFailedException() {
        super(HttpStatus.SERVICE_UNAVAILABLE, MESSAGE);
    }
}
