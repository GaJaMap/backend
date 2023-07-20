package com.map.gaja.client.presentation.api.specification;

import com.map.gaja.client.presentation.dto.access.ClientListAccessCheckDto;
import com.map.gaja.client.presentation.dto.request.ClientIdsRequest;
import com.map.gaja.client.presentation.dto.request.NewClientRequest;

import com.map.gaja.client.presentation.dto.request.simple.SimpleClientBulkRequest;
import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.global.exception.ValidationErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

public interface ClientCommandApiSpecification {

    @Operation(summary = "고객 삭제",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorInfo.class)))),
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
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorInfo.class)))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 그룹이 없거나 그룹에 요청 고객이 없음 <br> 또는 그룹 내에 사용자 수보다 많은 사용자를 제거할 수 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    @DeleteMapping("/group/{groupId}/clients/bulk")
    public ResponseEntity<Void> deleteBulkClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @Valid @RequestBody ClientIdsRequest clientIdsRequest
    );

    @Operation(summary = "고객 정보 변경", description = "form 형식으로 clientRequest 데이터를 전달해주세요",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorInfo.class)))),
                    @ApiResponse(responseCode = "415", description = "서버에서 지원하지 않는 파일 형식", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 그룹이 없거나, 그룹에 요청 고객이 없음 <br> 또는 그룹 내에 사용자 수보다 많은 사용자를 제거할 수 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류로 이미지 저장 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Void> updateClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @PathVariable Long groupId,
            @PathVariable Long clientId,
            @Valid @ModelAttribute NewClientRequest clientRequest,
            @Schema(hidden = true) BindingResult bindingResult
    ) throws BindException;


    @Operation(summary = "고객 등록", description = "form 형식으로 clientRequest 데이터를 전달해주세요",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공 - 고객 ID 반환", content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorInfo.class)))),
                    @ApiResponse(responseCode = "415", description = "서버에서 지원하지 않는 파일 형식", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 그룹이 없거나, 그룹에 요청 고객이 없음 <br> 또는 그룹 내에 사용자 수보다 많은 사용자를 등록할 수 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류로 이미지 저장 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Long> addClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @Valid @ModelAttribute NewClientRequest clientRequest,
            @Schema(hidden = true) BindingResult bindingResult
    ) throws BindException;

    @Operation(summary = "카카오, 전화번호부 데이터 등록",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공 - 생성된 고객 id에 대한 리스트", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Long.class)))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ValidationErrorInfo.class)))),
                    @ApiResponse(responseCode = "422", description = "사용자에게 요청 그룹이 없거나, 그룹에 요청 고객이 없음 <br> 또는 그룹 내에 사용자 수보다 많은 사용자를 등록할 수 없음", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    @PostMapping("/clients/bulk")
    public ResponseEntity<List<Long>> addSimpleBulkClient(
            @Schema(hidden = true) @AuthenticationPrincipal String loginEmail,
            @Valid @RequestBody SimpleClientBulkRequest clientBulkRequest
    );
}
