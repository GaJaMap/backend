package com.map.gaja.client.infrastructure.location.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotExcelUploadException extends BusinessException {
    public NotExcelUploadException() {
        super(HttpStatus.BAD_REQUEST, "현재 엑셀 업로드 서비스를 사용할 수 없습니다.");
    }

    public NotExcelUploadException(Throwable e) {
        super(e, HttpStatus.BAD_REQUEST, "현재 엑셀 업로드 서비스를 사용할 수 없습니다.");
    }
}
