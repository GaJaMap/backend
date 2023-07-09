package com.map.gaja.client.presentation.dto.request.simple;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleClientBulkRequest {

    @NotNull(message = "그룹 ID는 필수 입력 사항입니다.")
    @Schema(description = "등록할 그룹 ID 번호", example = "123")
    private Long groupId;

    @Valid
    @Size(min = 1, max = 300)
    private List<SimpleNewClientRequest> clients;
}
