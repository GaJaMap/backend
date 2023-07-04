package com.map.gaja.client.presentation.dto.response;

import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.subdto.GroupInfoDto;
import com.map.gaja.client.presentation.dto.subdto.StoredFileDto;
import lombok.*;

/**
 * 하나의 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long clientId;
//    private Long groupId;
    private GroupInfoDto groupInfo;
    private String clientName;
    private String phoneNumber;
    private ClientAddress address;
    private ClientLocation location;
    private StoredFileDto image;
    private Double distance; // km 기준
}
