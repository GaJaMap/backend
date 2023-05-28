package com.map.gaja.client.presentation.template;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;

/**
 * 예시용 코드이기 때문에 언제든지 삭제
 */
@Data
@NoArgsConstructor
public class ClientDto {
    private String name;
    @Max(value = 10, message = "ID는 최대 10")
    private long id;
}
