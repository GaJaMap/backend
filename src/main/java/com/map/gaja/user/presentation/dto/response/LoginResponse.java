package com.map.gaja.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    @Schema(description = "최근에 참조한 그룹아이디 => null이면 전체 고객 검색 api호출 / null이 아니면 특정 그룹 고객 전부 조회 api호출")
    private Long groupId;

    @Schema(description = "사용자 권한 등급 FREE(그룹1개, 각 그룹당 고객 200명), VIP(그룹500개, 각 그룹당 고객 1000명)")
    private String authority;
}
