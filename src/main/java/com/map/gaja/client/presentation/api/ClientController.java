package com.map.gaja.client.presentation.api;

import com.map.gaja.client.presentation.dto.response.ClientBulkResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long clientId) {
        // 거래처 조회
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClientBulkResponse> addClient(ClientBulkResponse clients ) {
        // 거래처 등록
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<ClientBulkResponse> addClients() {
        // 엑셀 등의 파일로 거래처 등록
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
