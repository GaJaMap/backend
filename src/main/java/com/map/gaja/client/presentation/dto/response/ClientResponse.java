package com.map.gaja.client.presentation.dto.response;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.presentation.dto.request.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.request.subdto.LocationDto;
import lombok.*;

/**
 * 하나의 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long clientId;
    private Long bundleId; // 번들 세부 정보는 나중에 추가.
    private String clientName;
    private String phoneNumber;
    private AddressDto address;
    private LocationDto location;
}
