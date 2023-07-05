package com.map.gaja.client.presentation.dto.request;

import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NearbyClientSearchRequest {
    @Schema(hidden = true)
    private LocationDto location; // 위도 경도
    @Schema(description = "검색 반경(M - 미터)", example = "3000")
    private Double radius; // 반경(미터)


    public NearbyClientSearchRequest() {
        location = new LocationDto();
    }

    @Schema(description = "현재 사용자 위도", example = "33.12345")
    public void setLatitude(double latitude) {
        this.location.setLatitude(latitude);
    }
    @Schema(description = "현재 사용자 경도", example = "127.7777")
    public void setLongitude(double longitude) {
        this.location.setLongitude(longitude);
    }
}
