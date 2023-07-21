package com.map.gaja.client.presentation.api.specification;

import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientDetailResponse;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.global.exception.ValidationErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

public interface GroupClientQueryApiSpecification {

    @Operation(summary = "특정 그룹내에 특정 고객 조회",
            description = "위치 정보가 없기 때문에 distance 필드는 null로 초기화됩니다.",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientOverviewResponse.class))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 번들이 없거나, 번들에 요청 고객이 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients/{clientId}")
    public ResponseEntity<ClientDetailResponse> getClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    );

    @Operation(summary = "특정 그룹내에 고객 전부 조회",
            description = "위치 정보가 없기 때문에 distance 필드는 null로 초기화됩니다.",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "wordCond", description = "조회할 고객 이름")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 번들이 없거나, 번들에 요청 고객이 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients")
    public ResponseEntity<ClientListResponse> getClientList(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @RequestParam(required = false) String wordCond
    );

    @Operation(summary = "특정 그룹내에 고객 반경 검색",
            description = "현재 사용자 위치에서 특정 그룹내에 고객들을 대상으로 반경 검색 - 가장 가까운 순으로 정렬 <br>" +
                    "만약 radius를 파라미터로 주지 않는다면, 고객의 위치 정보에 대해 반경으로 필터링을 하지 않고 모든 고객의 정보와 현재 위치에서 각각의 고객까지의 거리(distance)를 반환합니다.",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "wordCond", description = "조회할 고객 이름"),
                    @Parameter(name = "locationSearchCond", description = "반경 검색 조건", content = @Content(schema = @Schema(implementation = NearbyClientSearchRequest.class)))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientListResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorInfo.class)))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 번들이 없거나, 번들에 요청 고객이 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
            })
    @GetMapping("/api/group/{groupId}/clients/nearby")
    public ResponseEntity<ClientListResponse> nearbyClientSearch(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @Valid @ModelAttribute NearbyClientSearchRequest locationSearchCond,
            @RequestParam(required = false) String wordCond
    );
}
