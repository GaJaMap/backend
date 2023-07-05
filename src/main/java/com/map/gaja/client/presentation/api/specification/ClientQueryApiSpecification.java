package com.map.gaja.client.presentation.api.specification;

import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.response.CreatedClientResponse;
import com.map.gaja.global.exception.ExceptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients/{clientId}")
    public ResponseEntity<ClientResponse> getClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    );

    @Operation(summary = "특정 그룹내에 거래처 전부 조회",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients")
    public ResponseEntity<ClientListResponse> getClientList(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId
    );

    @Operation(summary = "반경 검색",
            description = "현재 사용자 위치에서 특정 그룹내에 고객들을 대상으로 반경 검색",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "wordCond", description = "조회할 거래처 이름"),
                    @Parameter(name = "locationSearchCond", description = "반경 검색 조건", content = @Content(schema = @Schema(implementation = NearbyClientSearchRequest.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    );

    @Operation(summary = "전체 고객들 반경 검색",
            description = "현재 사용자 위치에서 전체 고객들을 대상으로 반경 검색",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "wordCond", description = "조회할 거래처 이름"),
                    @Parameter(name = "locationSearchCond", description = "반경 검색 조건", content = @Content(schema = @Schema(implementation = NearbyClientSearchRequest.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    );
}
