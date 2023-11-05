package com.map.gaja.user.domain.exception;

import com.map.gaja.global.exception.BusinessException;
import org.springframework.http.HttpStatus;

// 이미지 업로드 기능 사용 시에 사용자 권한이 'FREE'라면 발생하는 예외
public class ImageUploadPermissionException extends BusinessException {

    public ImageUploadPermissionException(String authority) {
        super(HttpStatus.FORBIDDEN, String.format("회원님의 등급은 %s로 이미지 업로드 기능을 사용할 수 없습니다.", authority));
    }
}
