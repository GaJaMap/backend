package com.map.gaja.client.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 클라이언트 등록 시에 등록될 클라이언트 정보
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewClientRequest {
    private String clientName;
}
