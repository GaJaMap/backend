package com.map.gaja.client.presentation.api.specification;

import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.global.exception.ExceptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

public interface ClientQueryApiSpecification {
    @Operation(summary = "전체 고객 대상 반경 검색",
            description = "현재 사용자 위치에서 전체 고객들을 대상으로 반경 검색 - 가장 가까운 순으로 정렬",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "wordCond", description = "조회할 고객 이름"),
                    @Parameter(name = "locationSearchCond", description = "반경 검색 조건", content = @Content(schema = @Schema(implementation = NearbyClientSearchRequest.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 고객이 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @Valid @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    );

    @Operation(summary = "전체 고객 검색",
            description = "사용자가 가지고 있는 전체 고객 검색 - 생성일을 기준으로 최신순 정렬" +
                    "<br>위치 정보가 없기 때문에 distance 필드는 null로 초기화됩니다.",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "wordCond", description = "조회할 고객 이름"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 고객이 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/clients")
    public ResponseEntity<ClientListResponse> getAllClients(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @RequestParam(required = false) String wordCond
    );
}
