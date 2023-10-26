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

    @Pattern(regexp = "^[0-9]{7,12}$", message = "전화번호 형식 오류입니다. 하이픈(-)이 없고, 길이 7이상 12 이하의 숫자 문자열")
    @Schema(description = "전화번호", example = "010-1111-2222")
    private String phoneNumber;

    @Valid
    private AddressDto address = new AddressDto();

    @Valid
    private LocationDto location = new LocationDto();

    @Schema(description = "고객 사진 파일", example = "실제사진.jpg")
    private MultipartFile clientImage;

    @NotNull(message = "기본 이미지 유무는 필수 값입니다.")
    @Schema(description = "기본 이미지 인가요?<br> " +
            "해당 필드가 true라면 clientImage 필드를 넘겨주면 안됩니다.<br> " +
            "해당 필드가 false라면 clientImage 필드를 필수로 함께 넘겨줘야 합니다.", example = "FALSE")
    private Boolean isBasicImage;

    @JsonIgnore
    public void setMainAddress(String mainAddress) {
        this.address.setMainAddress(mainAddress);
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
