package com.map.gaja.client.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatedClientResponse {
    @Schema(description = "생성된 고객 ID 번호", example = "1234")
    private Long clientId;
}
