package com.map.gaja.client.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 여러 건의 등록할 사용자 정보
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewClientBulkRequest {
    private List<NewClientRequest> clients;
}
