package com.map.gaja.client.domain.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 예시용 코드이기 때문에 언제든지 삭제
 */
@Getter
@AllArgsConstructor
public class ClientException extends RuntimeException {
    private String message; // 에러 발생 내용
    private List<Long> errorClientId; // 에러가 발생한 클라이언트 ID
    private int successClientCount; // 성공한 클라이언트 수
}
