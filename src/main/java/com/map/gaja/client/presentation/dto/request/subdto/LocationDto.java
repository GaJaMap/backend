package com.map.gaja.client.presentation.dto.request.subdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    @Schema(description = "현재 사용자 위도", example = "33.12345")
    private Double latitude;
    @Schema(description = "현재 사용자 경도", example = "127.7777")
    private Double longitude;
}
