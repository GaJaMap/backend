package com.map.gaja.group.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class GroupResponse {
    @Schema(description = "다음 페이지가 있는 지")
    private boolean hasNext;

    private List<GroupInfo> groupInfos;

    public GroupResponse(boolean hasNext, List<GroupInfo> groupInfos) {
        this.hasNext = hasNext;
        this.groupInfos = groupInfos;
    }
}
