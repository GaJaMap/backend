package com.map.gaja.client.presentation.dto.response;

import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import lombok.*;

/**
 * 하나의 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long clientId;
    private Long groupId; // 번들 세부 정보는 나중에 추가.
    private String clientName;
    private String phoneNumber;
    private ClientAddress address;
    private ClientLocation location;
    private Double distance; // km 기준
}
