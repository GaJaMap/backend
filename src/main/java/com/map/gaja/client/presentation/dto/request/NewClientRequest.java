package com.map.gaja.client.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 클라이언트 등록 시에 등록될 클라이언트 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewClientRequest {
    @Schema(description = "고객 이름", example = "홍길동")
    private String clientName;
    @Schema(description = "등록할 그룹 ID 번호", example = "123")
    private Long groupId;
    @Schema(description = "전화번호", example = "010-1111-2222")
    private String phoneNumber;
    private AddressDto address = new AddressDto();
    private LocationDto location = new LocationDto();
    @Schema(description = "고객 사진 파일", example = "실제사진.jpg")
    private MultipartFile clientImage;

    @JsonIgnore
    public void setProvince(String province) {
        address.setProvince(province);
    }
    @JsonIgnore
    public void setCity(String city) {
        address.setCity(city);
    }
    @JsonIgnore
    public void setDistrict(String district) {
        address.setDistrict(district);
    }
    @JsonIgnore
    public void setDetail(String detail) {
        address.setDetail(detail);
    }
    @JsonIgnore
    public void setLatitude(Double latitude) {
        location.setLatitude(latitude);
    }
    @JsonIgnore
    public void setLongitude(Double longitude) {
        location.setLongitude(longitude);
    }
}
