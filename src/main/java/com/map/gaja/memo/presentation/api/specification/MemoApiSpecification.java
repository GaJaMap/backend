package com.map.gaja.memo.presentation.api.specification;

import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;
import com.map.gaja.memo.presentation.dto.response.MemoPageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface MemoApiSpecification {
    @Operation(summary = "메모 생성",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "clientId", description = "Client ID")
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "응답 값: 메모 id", content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 클라이언트입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Long> create(
            @Schema(hidden = true) Long userId,
            Long clientId,
            MemoCreateRequest request
    );

    @Operation(summary = "메모 삭제",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "memoId", description = "Memo ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 메모입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Void> delete(@Parameter(hidden = true) Long userId, Long memoId);

    @Operation(summary = "메모 페이징 조회",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "clientId", description = "Client ID"),
                    @Parameter(name = "page", description = "페이지 번호 처음은 0번 페이지")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 클라이언트입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<MemoPageResponse> read(@Parameter(hidden = true) Long userId, Long clientId, Pageable pageable);
}
