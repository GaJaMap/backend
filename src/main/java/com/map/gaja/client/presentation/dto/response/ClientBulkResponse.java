package com.map.gaja.client.presentation.dto.response;

import lombok.*;

import java.util.List;

/**
 * 여러 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientBulkResponse {
    List<ClientResponse> clients;
}
