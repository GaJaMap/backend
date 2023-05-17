package com.map.gaja.client.presentation.dto.request;

import com.map.gaja.client.presentation.dto.subdto.LocationDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NearbyClientSearchRequest {
    private LocationDto location; // 위도 경도
    private double radius; // 반경(미터)

    public NearbyClientSearchRequest() {
        location = new LocationDto();
    }

    public void setLatitude(double latitude) {
        this.location.setLatitude(latitude);
    }

    public void setLongitude(double longitude) {
        this.location.setLongitude(longitude);
    }
}
