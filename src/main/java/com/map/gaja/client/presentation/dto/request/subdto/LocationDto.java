package com.map.gaja.client.presentation.dto.request.subdto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    @Range(min = -90, max = 90)
    @Schema(description = "현재 사용자 위도", example = "33.12345")
    private Double latitude;
    @Range(min = -180, max = 180)
    @Schema(description = "현재 사용자 경도", example = "127.7777")
    private Double longitude;
}
