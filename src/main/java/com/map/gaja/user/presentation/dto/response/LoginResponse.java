package com.map.gaja.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {
    @Schema(description = "이메일")
    private String email;

    @Schema(description = "사용자 권한 등급 FREE(그룹1개, 각 그룹당 고객 200명), VIP(그룹500개, 각 그룹당 고객 1000명)")
    private String authority;

    @Schema(description = "생성일")
    private String createdDate;



}
