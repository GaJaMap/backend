package com.map.gaja.client.presentation.dto.request;

import lombok.*;

/**
 * 클라이언트 등록 시에 등록될 클라이언트 정보
 */
@Data
public class NewClientRequest {
    private String clientName;
    private Long groupId;
}
