package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.response.ClientDeleteResponse;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @DeleteMapping("/{clientId}")
    public ResponseEntity<ClientDeleteResponse> deleteClient(@PathVariable Long clientId) {
        // 거래처 삭제
        log.info("ClientController.deleteClient clinetId={}", clientId);
        ClientDeleteResponse response = clientService.deleteClient(clientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClientListResponse> addClient(@RequestBody NewClientBulkRequest clients) {
        // 거래처 등록
        log.info("ClientController.addClient  clients={}", clients);
        ClientListResponse response = clientService.saveClients(clients);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<ClientListResponse> addClients(@RequestParam MultipartFile file) {
        // 엑셀 등의 파일로 거래처 등록
        log.info("ClientController.addClients");
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }
}
