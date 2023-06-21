package com.map.gaja.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginRequest {
    @Schema(description = "카카오 액세스 토큰")
    private String accessToken;
}
