package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ReadOnly Client 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class GetClientController {
    private final ClientQueryService clientQueryService;

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long clientId) {
        // 거래처 조회
        log.info("ClientController.getClient clinetId={}", clientId);
        ClientResponse response = clientQueryService.findUser(clientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ClientListResponse> searchClientsByCondition(@RequestParam String name) {
        log.info("GetClientController.searchClientsByCondition name={}",name);
        // 거래처 조건 조회
        ClientListResponse response = clientQueryService.findUser(name);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(NearbyClientSearchRequest nearby) {
        // 주변 거래처 조회
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
