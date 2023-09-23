package com.map.gaja.client.presentation.dto.request.subdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @Size(max = 40)
    @Schema(description = "도로명 또는 지번 주소", example = "서울특별시 중구 세종대로 110")
    private String mainAddress;
    @Size(max = 20, message = "주소 요소는 30자 이하로 입력해주세요.")
    @Schema(description = "상세주소", example = "2층 205호")
    private String detail; // 상세주소
}
