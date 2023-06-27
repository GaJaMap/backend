package com.map.gaja.client.presentation.api;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.response.ClientSliceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ReadOnly Client 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/bundle/{bundleId}/clients")
@RequiredArgsConstructor
public class GetClientController {
    private final ClientQueryService clientQueryService;

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClient(@PathVariable Long clientId) {
        // 거래처 조회
        log.info("ClientController.getClient clinetId={}", clientId);
        ClientResponse response = clientQueryService.findClient(clientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ClientListResponse> getClientList(@PathVariable Long bundleId) {
        // 특정 번들 내에 모든 거래처 조회
        log.info("GetClientController.getClientList bundleId={}", bundleId);
        ClientListResponse response = clientQueryService.findAllClientsInBundle(bundleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/nearby")
    public ResponseEntity<ClientSliceResponse> nearbyClientSearch(
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @PathVariable Long bundleId,
            @RequestParam(required = false) String wordCond,
            @PageableDefault Pageable pageable
            ) {
        // 주변 거래처 조회
        log.info("GetClientController.nearbyClientSearch params={},{},{},{}", bundleId, locationSearchCond, wordCond, pageable);
        ClientSliceResponse response = clientQueryService.findClientByConditions(bundleId, locationSearchCond, wordCond, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
