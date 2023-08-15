package com.map.gaja.user.presentation.dto.response;

import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AutoLoginResponse {
    @Schema(description = "고객 api 호출한 클래스와 동일")
    private ClientListResponse clientListResponse;

    @Schema(description = "최근에 참조한 그룹 정보")
    private GroupInfo groupInfo;
}
