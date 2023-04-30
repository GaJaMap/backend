package com.map.gaja.client.presentation.template;


import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 예시용 코드이기 때문에 언제든지 삭제
 */
@Data
@Builder
public class ClientBatchFailureResponse {
    private String code; // 특정 반환 코드 - enum 가능
    private String message; // 에러 메시지
    private List<Long> errorClientId; // 에러가 발생한 클라이언트 ID
    private int successClientCount; // 성공한 클라이언트 정보
}
