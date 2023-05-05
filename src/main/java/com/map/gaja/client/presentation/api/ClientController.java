package com.map.gaja.client.presentation.api;

import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/clients")
public class ClientController {

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long clientId) {
        // 거래처 조회
        log.info("ClientController.getClient clinetId={}", clientId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/nearby")
    public ResponseEntity<ClientListResponse> getClient(NearbyClientSearchRequest nearby) {
        // 주변 거래처 조회
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<ClientResponse> deleteClient(@PathVariable Long clientId) {
        // 거래처 삭제
        log.info("ClientController.deleteClient clinetId={}", clientId);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClientListResponse> addClient(@RequestBody NewClientBulkRequest clients) {
        // 거래처 등록
        log.info("ClientController.addClient  clients={}", clients);
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<ClientListResponse> addClients(@RequestParam MultipartFile file) {
        // 엑셀 등의 파일로 거래처 등록
        log.info("ClientController.addClients");
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
