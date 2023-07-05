package com.map.gaja.client.presentation.dto.request.subdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    @Schema(description = "도/시/특별시", example = "인천시")
    private String province; // 도/시/특별시
    @Schema(description = "구/군", example = "부평구")
    private String city; // 시/군/구
    @Schema(description = "도로명 주소", example = "부평대로 168")
    private String district; //읍/면/동
    @Schema(description = "상세주소", example = "2층 205호")
    private String detail; // 상세주소
}
