package com.map.gaja.client.presentation.api;

import com.map.gaja.bundle.application.BundleAccessVerifyService;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.response.ClientSliceResponse;
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
@RequestMapping("/api/bundle/{bundleId}/clients")
@RequiredArgsConstructor
public class GetClientController {
    private final ClientQueryService clientQueryService;
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final BundleAccessVerifyService bundleAccessVerifyService;

    @GetMapping("/{clientId}")
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

    @GetMapping
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

    @GetMapping("/nearby")
    public ResponseEntity<ClientSliceResponse> nearbyClientSearch(
            @LoginEmail String loginEmail,
            @PathVariable Long bundleId,
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    ) {
        // 주변 거래처 조회
        log.info("GetClientController.nearbyClientSearch params={},{},{}", bundleId, locationSearchCond, wordCond);
        verifyBundleAccess(loginEmail, bundleId);
        ClientSliceResponse response = clientQueryService.findClientByConditions(bundleId, locationSearchCond, wordCond);
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
