package com.map.gaja.client.presentation.api.specification;

import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.response.CreatedClientResponse;
import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

public interface ClientCommandApiSpecification {

    @Operation(summary = "거래처 삭제",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Void> deleteClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    );

    @Operation(summary = "거래처 정보 변경", description = "form 형식으로 clientRequest 데이터를 전달해주세요",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
                    @ApiResponse(responseCode = "400", description = "위치 정보가 한국을 벗어남", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류로 이미지 저장 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<ClientResponse> changeClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId,
            @ModelAttribute NewClientRequest clientRequest
    );


    @Operation(summary = "거래처 등록", description = "form 형식으로 clientRequest 데이터를 전달해주세요",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공", content = @Content(schema = @Schema(implementation = ClientResponse.class))),
                    @ApiResponse(responseCode = "400", description = "사용자 정보가 한국을 벗어남", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "사용자에게 요청 번들이 없거나, 번들에 요청 client가 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류로 이미지 저장 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<CreatedClientResponse> addClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @ModelAttribute NewClientRequest clientRequest
    );
}
