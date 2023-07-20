package com.map.gaja.location.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class NotExcelUploadException extends BusinessException {
    public NotExcelUploadException() {
        super(HttpStatus.BAD_REQUEST, "현재 엑셀 업로드 서비스를 사용할 수 없습니다.");
    }
}
