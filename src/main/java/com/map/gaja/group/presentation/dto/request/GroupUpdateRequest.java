package com.map.gaja.group.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupUpdateRequest {
    @Schema(description = "바꿀 그룹 이름")
    private String name;
}