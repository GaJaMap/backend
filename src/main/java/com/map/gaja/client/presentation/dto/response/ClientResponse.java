package com.map.gaja.client.presentation.dto.response;

import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 하나의 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    @Schema(description = "고객 등록 ID 번호", example = "111")
    private Long clientId;
//    private Long groupId;
    @Schema(description = "고객이 속한 그룹")
    private GroupInfoDto groupInfo;
    @Schema(description = "고객 이름", example = "홍길동")
    private String clientName;
    @Schema(description = "고객 전화번호", example = "010-3333-4444")
    private String phoneNumber;
    @Schema(description = "고객 주소")
    private ClientAddress address;
    @Schema(description = "고객 위치")
    private ClientLocation location;
    @Schema(description = "고객 사진")
    private StoredFileDto image;
    @Schema(description = "현재 위치에서 떨어진 거리(M - 미터)", example = "2398")
    private Double distance; // km 기준
}
