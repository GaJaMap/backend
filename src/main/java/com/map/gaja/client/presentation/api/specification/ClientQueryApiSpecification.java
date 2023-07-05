package com.map.gaja.client.presentation.api.specification;

import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.response.CreatedClientResponse;
import com.map.gaja.global.exception.ExceptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface ClientQueryApiSpecification {

    @Operation(summary = "특정 그룹내에 특정 거래처 조회",
            parameters = {
                    @Parameter(name = "loginEmail", description = "로그인 이메일"),
                    @Parameter(name = "groupId", description = "조회할 그룹 ID"),
                    @Parameter(name = "clientId", description = "조회할 거래처 ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients/{clientId}")
    public ResponseEntity<ClientResponse> getClient(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    );

    @Operation(summary = "특정 그룹내에 거래처 전부 조회",
            parameters = {
                    @Parameter(name = "loginEmail", description = "로그인 이메일"),
                    @Parameter(name = "groupId", description = "조회할 그룹 ID"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients")
    public ResponseEntity<ClientListResponse> getClientList(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId
    );

    @Operation(summary = "특정 그룹내에 거래처 반경 조회",
            parameters = {
                    @Parameter(name = "loginEmail", description = "로그인 이메일"),
                    @Parameter(name = "groupId", description = "조회할 그룹 ID"),
                    @Parameter(name = "wordCond", description = "조회할 거래처 이름"),
                    @Parameter(name = "locationSearchCond", description = "반경 검색 조건", required = true, content = @Content(schema = @Schema(implementation = NearbyClientSearchRequest.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    );

    @Operation(summary = "전체 거래처 반경 조회",
            parameters = {
                    @Parameter(name = "loginEmail", description = "로그인 이메일"),
                    @Parameter(name = "wordCond", description = "조회할 거래처 이름"),
                    @Parameter(name = "locationSearchCond", description = "반경 검색 조건", required = true, content = @Content(schema = @Schema(implementation = NearbyClientSearchRequest.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @AuthenticationPrincipal String loginEmail,
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    );
}
