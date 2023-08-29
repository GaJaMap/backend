package com.map.gaja.client.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * 여러 등록된 사용자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientListResponse {
    @Schema(description = "고객 리스트")
    List<ClientOverviewResponse> clients;
}
