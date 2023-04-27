package com.map.gaja.client.presentation.api;

import com.map.gaja.client.domain.exception.ClientException;
import com.map.gaja.client.presentation.dto.ClientBatchDto;
import com.map.gaja.client.presentation.dto.ClientDto;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예시용 코드이기 때문에 언제든지 삭제
 */
@RestController
@RequestMapping("/api/client")
public class ClientController {

    /**
     * ClientException 발생을 위한 컨트롤러
     */
    @PostMapping("/test")
    public String testBatch(@RequestBody @Valid ClientBatchDto batchClients) {
        List<Long> result = batchClients.getClients().stream()
                .filter(c -> c.getId() == 1L || c.getId() == 2L)
                .map(ClientDto::getId)
                .collect(Collectors.toList());

        throw new ClientException("테스트 메시지", result, 2);
    }
}
