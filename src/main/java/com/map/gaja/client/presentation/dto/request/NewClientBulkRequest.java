package com.map.gaja.client.presentation.dto.request;

import lombok.*;

import java.util.List;

/**
 * 여러 건의 등록할 사용자 정보
 */
@Data
public class NewClientBulkRequest {
    private List<NewClientRequest> clients;
}
