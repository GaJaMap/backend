package com.map.gaja.client.presentation.dto.subdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
public class GroupDetailDto extends GroupInfoDto {
    @Schema(description = "그룹에 속한 고객수")
    private int clientCount;

    public GroupDetailDto(Long groupId, String groupName, int clientCount) {
        super(groupId, groupName);
        this.clientCount = clientCount;
    }
}
