package com.map.gaja.client.presentation.dto.response;

import com.map.gaja.client.domain.model.Client;
import lombok.*;

/**
 * 하나의 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
    private Long clientId;
    private String name;

    public ClientResponse(Client client) {
        clientId = client.getId();
        name = client.getName();
    }
}
