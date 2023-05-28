package com.map.gaja.client.presentation.template;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

/**
 * 예시용 코드이기 때문에 언제든지 삭제
 */
@Data
public class ClientBatchDto {
    @Valid
    private List<ClientDto> clients;
}
