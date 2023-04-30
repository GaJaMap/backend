package com.map.gaja.client.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 여러 등록된 사용자 정보
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientBulkResponse {
    List<ClientResponse> clients;
}
