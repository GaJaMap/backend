package com.map.gaja.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponse {
    @Schema(description = "Null이 허용되지 않았는데 null로 들어오면 NotNull, 전화번호 패턴이 안 맞으면 Pattern", example = "NotNull")
    private String code;
    @Schema(description = "에러가 발생한 객체명", example = "newClientRequest")
    private String objectName;
    @Schema(description = "에러에 대한 설명", example = "고객 이름은 필수 입력 사항입니다.")
    private String message;
}
