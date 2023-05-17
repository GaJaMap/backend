package com.map.gaja.client.presentation.dto.request;

import com.map.gaja.client.presentation.dto.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.subdto.LocationDto;
import lombok.*;

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
    private AddressDto address;
    private LocationDto location;
}
