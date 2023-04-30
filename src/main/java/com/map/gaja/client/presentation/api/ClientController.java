package com.map.gaja.client.presentation.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @GetMapping("/{clientId}")
    public void getClient(@PathVariable Long clientId) {
        // 거래처 조회
    }

    @PostMapping
    public void addClient() {
        // 거래처 등록
    }

    @PostMapping("/bulk")
    public void addClients() {
        // 엑셀 등의 파일로 거래처 등록
    }
}
