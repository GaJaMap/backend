package com.map.gaja.client.presentation.api;

import com.map.gaja.bundle.application.BundleAccessVerifyService;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.global.annotation.LoginEmail;
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
@RequiredArgsConstructor
public class GetClientController {
    private final ClientQueryService clientQueryService;
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final BundleAccessVerifyService bundleAccessVerifyService;

    @GetMapping("/api/bundle/{bundleId}/clients/{clientId}")
    public ResponseEntity<ClientResponse> getClient(
            @LoginEmail String loginEmail,
            @PathVariable Long bundleId,
            @PathVariable Long clientId
    ) {
        // 거래처 조회
        log.info("ClientController.getClient clientId={}", clientId);
        verifyClientAccess(loginEmail,bundleId,clientId);
        ClientResponse response = clientQueryService.findClient(clientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/bundle/{bundleId}/clients")
    public ResponseEntity<ClientListResponse> getClientList(
            @LoginEmail String loginEmail,
            @PathVariable Long bundleId
    ) {
        // 특정 번들 내에 모든 거래처 조회
        log.info("GetClientController.getClientList bundleId={}", bundleId);
        verifyBundleAccess(loginEmail, bundleId);
        ClientListResponse response = clientQueryService.findAllClientsInBundle(bundleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/bundle/{bundleId}/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @LoginEmail String loginEmail,
            @PathVariable Long bundleId,
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    ) {
        // 주변 거래처 조회
        log.info("GetClientController.nearbyClientSearch params={},{},{}", bundleId, locationSearchCond, wordCond);
        verifyBundleAccess(loginEmail, bundleId);
        ClientListResponse response = clientQueryService.findClientByConditions(bundleId, locationSearchCond, wordCond);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/bundle/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @LoginEmail String loginEmail,
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    ) {
        // 주변 거래처 조회
        log.info("GetClientController.nearbyClientSearch params={},{},{}", loginEmail, locationSearchCond, wordCond);
        ClientListResponse response = clientQueryService.findClientByConditions(loginEmail, locationSearchCond, wordCond);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void verifyClientAccess(String loginEmail, Long bundleId, Long clientId) {
        ClientAccessCheckDto accessCheckDto = new ClientAccessCheckDto(loginEmail, bundleId, clientId);
        clientAccessVerifyService.verifyClientAccess(accessCheckDto);
    }

    private void verifyBundleAccess(String loginEmail, long bundleId) {
        bundleAccessVerifyService.verifyBundleAccess(bundleId, loginEmail);
    }
}
