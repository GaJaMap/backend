package com.map.gaja.client.presentation.dto.subdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInfoDto {
    @Schema(description = "등록된 그룹 ID 번호", example = "100")
    private Long groupId;
    @Schema(description = "그룹 이름", example = "서울시 고객들")
    private String groupName;
}
