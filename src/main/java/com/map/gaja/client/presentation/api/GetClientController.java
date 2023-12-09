package com.map.gaja.client.presentation.api;

import com.map.gaja.client.application.ClientQueryService;
import com.map.gaja.client.presentation.api.specification.ClientQueryApiSpecification;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.global.log.TimeCheckLog;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Timed("client.search")
@TimeCheckLog
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clients")
public class GetClientController implements ClientQueryApiSpecification {

    private final ClientQueryService clientQueryService;

    @GetMapping("/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @Valid @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    ) {
        // 주변 거래처 조회
        ClientListResponse response = clientQueryService.findClientByConditions(loginEmail, locationSearchCond, wordCond);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ClientListResponse> getAllClients(
            @AuthenticationPrincipal(expression = "name") String loginEmail,
            @RequestParam(required = false) String wordCond
    ) {
        // 사용자가 가지고 있는 전체 고객 조회
        ClientListResponse response = clientQueryService.findAllClient(loginEmail, wordCond);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
