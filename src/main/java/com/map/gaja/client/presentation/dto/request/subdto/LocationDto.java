package com.map.gaja.client.presentation.dto.request.subdto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.locationtech.jts.geom.Point;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
//    @NotNull
    @Range(min = 33, max = 39, message = "위도는 한국 위도 범위인 +33.0 이상, +39 이하만 가능합니다.")
    @Schema(description = "현재 사용자 위도", example = "33.12345")
    private Double latitude;

//    @NotNull
    @Range(min = 124, max = 132, message = "경도는 한국 경도 범위인 +124.0 이상, +132.0 이하만 가능합니다.")
    @Schema(description = "현재 사용자 경도", example = "127.7777")
    private Double longitude;

    @JsonIgnore
    public LocationDto(Point location) {
        if (location == null) {
            latitude = null;
            longitude = null;
            return;
        }

        latitude = location.getY();
        longitude = location.getX();
    }
}
