package com.map.gaja.client.presentation.dto.response;

import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Client 상세보기 Data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetailResponse {
    @Schema(description = "고객 등록 ID 번호", example = "111")
    private Long clientId;

    @Schema(description = "고객이 속한 그룹")
    private GroupInfoDto groupInfo;

    @Schema(description = "고객 이름", example = "홍길동")
    private String clientName;

    @Schema(description = "고객 전화번호", example = "010-3333-4444")
    private String phoneNumber;

    @Schema(description = "개인적인 사용자의 메모", example = "55세, 기업인, 창업주 이병철의 손자이자 홍진기의 외손자")
    private String memo;

    @Schema(description = "고객 주소")
    private AddressDto address;

    @Schema(description = "고객 위치")
    private LocationDto location;

    @Schema(description = "고객 사진")
    private StoredFileDto image;

    @Schema(description = "현재 위치에서 떨어진 거리(M - 미터)", example = "2398")
    private Double distance; // km 기준

    private LocalDateTime createdAt;
}
