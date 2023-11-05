package com.map.gaja.client.presentation.dto.request.simple;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleNewClientRequest {
    @NotNull(message = "고객 이름은 필수 입력 사항입니다.")
    @Size(max = 20, message = "고객 이름은 20자 이하로 입력해 주세요.")
    @Schema(description = "고객 이름", example = "홍길동")
    private String clientName;

    @Pattern(regexp = "^[0-9]{7,12}$", message = "전화번호 형식 오류입니다. 하이픈(-)이 없고, 길이 7이상 12 이하의 숫자 문자열")
    @Schema(description = "전화번호", example = "01011112222")
    private String phoneNumber;
}
