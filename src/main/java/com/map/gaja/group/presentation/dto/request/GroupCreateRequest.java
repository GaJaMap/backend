package com.map.gaja.group.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateRequest {
    @Schema(description = "그룹 이름")
    @NotNull(message = "그룹 이름은 필수 입력 사항입니다.")
    @Size(max = 20, message = "그룹 이름은 20자 이하로 입력해 주세요.")
    private String name;
}
