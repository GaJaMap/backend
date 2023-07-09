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

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식 오류입니다.")
    @Schema(description = "전화번호", example = "010-1111-2222")
    private String phoneNumber;
}
