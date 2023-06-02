package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.ClientService;
import com.map.gaja.client.presentation.dto.request.NewClientBulkRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
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
    public ResponseEntity<Void> deleteClient(@PathVariable Long clientId) {
        // 거래처 삭제
        log.info("ClientController.deleteClient clinetId={}", clientId);
        clientService.deleteClient(clientId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClientResponse> addClient(@RequestBody NewClientRequest client) {
        // 거래처 등록 - 단건 등록
        log.info("ClientController.addClient  clients={}", client);
        ClientResponse response = clientService.saveClient(client);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    public ResponseEntity<ClientListResponse> addBulkClient(@RequestBody NewClientBulkRequest clients) {
        // 거래처 등록 - 여러건 등록
        log.info("ClientController.addBulkClient  clients={}", clients);
        ClientListResponse response = clientService.saveClientList(clients);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bulk/file")
    public ResponseEntity<ClientListResponse> addClients(@RequestParam MultipartFile file) {
        // 엑셀 등의 파일로 거래처 등록
        log.info("ClientController.addClients");
        ClientListResponse response = clientService.parseFileAndSave(file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> changeClient(@PathVariable Long clientId, @RequestBody NewClientRequest clientRequest) {
        // 기존 거래처 정보 변경
        // User가 Client를 가지고 있는가? + 해당 번들을 User가 가지고 있는가?
        log.info("ClientController.changeClients clientRequest={}", clientRequest);
        ClientResponse response = clientService.changeClient(clientId, clientRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
