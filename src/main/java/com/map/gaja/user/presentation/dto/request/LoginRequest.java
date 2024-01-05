package com.map.gaja.user.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginRequest {
    @Schema(description = "카카오 액세스 토큰")
    private String accessToken;

}
