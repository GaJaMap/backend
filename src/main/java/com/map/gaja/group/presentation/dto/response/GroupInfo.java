package com.map.gaja.group.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public interface GroupInfo {
    @Schema(description = "그룹 아이디")
    Long getGroupId();

    @Schema(description = "그룹 이름")
    String getGroupName();

    @Schema(description = "그룹에 속한 고객수")
    Integer getClientCount();
}
