package com.map.gaja.client.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientIdsRequest {
    @Size(min = 1, message = "요청 시 id의 개수는 무조건 1개 이상")
    @Schema(description = "고객 ID 리스트", example = "100")
    private List<Long> clientIds;
}
