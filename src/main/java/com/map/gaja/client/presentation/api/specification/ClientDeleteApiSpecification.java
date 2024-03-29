package com.map.gaja.client.presentation.api.specification;

import com.map.gaja.client.presentation.dto.request.ClientIdsRequest;
import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.global.exception.ValidationErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface ClientDeleteApiSpecification {
    @Operation(summary = "고객 삭제",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorResponse.class)))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 그룹이 없거나 그룹에 요청 고객이 없음 <br> 또는 그룹 내에 사용자 수보다 많은 사용자를 제거할 수 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Void> deleteClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId
    );

    @Operation(summary = "다수 고객 삭제",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorResponse.class)))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 그룹이 없거나 그룹에 요청 고객이 없음 <br> 또는 그룹 내에 사용자 수보다 많은 사용자를 제거할 수 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    public ResponseEntity<Void> deleteBulkClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @Valid @RequestBody ClientIdsRequest clientIdsRequest
    );
}
