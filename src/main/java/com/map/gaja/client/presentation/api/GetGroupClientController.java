package com.map.gaja.client.presentation.api;

import com.map.gaja.client.presentation.api.specification.GroupClientQueryApiSpecification;
import com.map.gaja.client.presentation.dto.response.ClientDetailResponse;
import com.map.gaja.global.log.TimeCheckLog;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.client.apllication.ClientAccessVerifyService;
import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.presentation.dto.access.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * ReadOnly Client 컨트롤러
 */
@TimeCheckLog
@RestController
@RequiredArgsConstructor
public class GetGroupClientController implements GroupClientQueryApiSpecification {
    private final ClientQueryService clientQueryService;
    private final ClientAccessVerifyService clientAccessVerifyService;
    private final GroupAccessVerifyService groupAccessVerifyService;

    @GetMapping("/api/group/{groupId}/clients/{clientId}")
    public ResponseEntity<ClientDetailResponse> getClient(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    ) {
        // 거래처 조회
        verifyClientAccess(loginEmail,groupId,clientId);
        ClientDetailResponse response = clientQueryService.findClient(clientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/group/{groupId}/clients")
    public ResponseEntity<ClientListResponse> getClientList(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @RequestParam(required = false) String wordCond
    ) {
        // 특정 번들 내에 모든 거래처 조회
        verifyGroupAccess(loginEmail, groupId);
        ClientListResponse response = clientQueryService.findAllClientsInGroup(groupId, wordCond);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/group/{groupId}/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @Valid @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    ) {
        // 주변 거래처 조회
        verifyGroupAccess(loginEmail, groupId);
        ClientListResponse response = clientQueryService.findClientByConditions(groupId, locationSearchCond, wordCond);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void verifyClientAccess(String loginEmail, Long groupId, Long clientId) {
        ClientAccessCheckDto accessCheckDto = new ClientAccessCheckDto(loginEmail, groupId, clientId);
        clientAccessVerifyService.verifyClientAccess(accessCheckDto);
    }

    private void verifyGroupAccess(String loginEmail, long groupId) {
        groupAccessVerifyService.verifyGroupAccess(groupId, loginEmail);
    }
}
