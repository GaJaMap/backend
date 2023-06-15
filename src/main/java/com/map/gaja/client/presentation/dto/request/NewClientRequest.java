package com.map.gaja.client.presentation.dto.request;

import com.map.gaja.client.presentation.dto.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.subdto.LocationDto;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 클라이언트 등록 시에 등록될 클라이언트 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewClientRequest {
    private String clientName;
    private Long bundleId;
    private String phoneNumber;
    private AddressDto address = new AddressDto();
    private LocationDto location = new LocationDto();

    private MultipartFile clientImage;

    public void setProvince(String province) {
        address.setProvince(province);
    }

    public void setCity(String city) {
        address.setCity(city);
    }

    public void setDistrict(String district) {
        address.setDistrict(district);
    }

    public void setDetail(String detail) {
        address.setDetail(detail);
    }

    public void setLatitude(Double latitude) {
        location.setLatitude(latitude);
    }

    public void setLongitude(Double longitude) {
        location.setLongitude(longitude);
    }
}
