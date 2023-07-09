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
    @Range(min = -90, max = 90, message = "위도는 -90.0 이상, +90 이하 입니다.")
    @Schema(description = "현재 사용자 위도", example = "33.12345")
    private Double latitude;
    @Range(min = -180, max = 180, message = "경도는 -180.0 이상, +180.0 이하 입니다.")
    @Schema(description = "현재 사용자 경도", example = "127.7777")
    private Double longitude;
}
