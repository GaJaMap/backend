package com.map.gaja.client.presentation.dto.response;

import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Client 상세보기 Data
 */
public class ClientDetailResponse {
    @Schema(description = "고객 등록 ID 번호", example = "111")
    private Long clientId;

    @Schema(description = "고객이 속한 그룹")
    private GroupInfoDto groupInfo;

    @Schema(description = "고객 이름", example = "홍길동")
    private String clientName;

    @Schema(description = "고객 전화번호", example = "010-3333-4444")
    private String phoneNumber;

    @Schema(description = "고객 주소")
    private AddressDto address;

    @Schema(description = "고객 위치")
    private LocationDto location;

    @Schema(description = "고객 사진")
    private StoredFileDto image;

    @Schema(description = "현재 위치에서 떨어진 거리(M - 미터)", example = "2398")
    private Double distance; // km 기준

    public ClientDetailResponse(Long clientId, GroupInfoDto groupInfo, String clientName, String phoneNumber, AddressDto address, LocationDto location, StoredFileDto image) {
        this.clientId = clientId;
        this.groupInfo = groupInfo;
        this.clientName = clientName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.location = location;
        this.image = image;
    }
}
