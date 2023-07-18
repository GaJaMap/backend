package com.map.gaja.client.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 클라이언트 등록 시에 등록될 클라이언트 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewClientRequest {
    @NotNull(message = "고객 이름은 필수 입력 사항입니다.")
    @Size(max = 20, message = "고객 이름은 20자 이하로 입력해 주세요.")
    @Schema(description = "고객 이름", example = "홍길동")
    private String clientName;

    @NotNull(message = "그룹 ID는 필수 입력 사항입니다.")
    @Schema(description = "등록할 그룹 ID 번호", example = "123")
    private Long groupId;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식 오류입니다.")
    @Schema(description = "전화번호", example = "010-1111-2222")
    private String phoneNumber;

    @Valid
    private AddressDto address = new AddressDto();

    @Valid
    private LocationDto location = new LocationDto();

    @Schema(description = "고객 사진 파일", example = "실제사진.jpg")
    private MultipartFile clientImage;

    @Schema(description = "기본 이미지 인가요?", example = "FALSE")
    private boolean isBasicImage;

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
