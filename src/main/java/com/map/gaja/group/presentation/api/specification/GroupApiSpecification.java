package com.map.gaja.group.presentation.api.specification;

import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.group.presentation.dto.request.GroupUpdateRequest;
import com.map.gaja.group.presentation.dto.response.GroupResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface GroupApiSpecification {
    @Operation(summary = "그룹 생성",
            parameters = {@Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER)},
            responses = {
                    @ApiResponse(responseCode = "201", description = "응답 값: 그룹 id", content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "403", description = "권한에 따른 그룹 생성 초과", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Long> create(
            @Schema(hidden = true) Long userId,
            GroupCreateRequest request
    );

    @Operation(summary = "그룹 조회",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "page", description = "페이지 번호 0부터 시작")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = GroupResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<GroupResponse> read(@Parameter(hidden = true) Long userId, @Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "그룹 삭제",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "groupId", description = "그룹 아이디")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "422", description = "존재하지 않은 그룹이거나 사용자의 그룹이 아닙니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Void> delete(@Parameter(hidden = true) Long userId, Long groupId);

    @Operation(summary = "그룹 수정",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
                    @Parameter(name = "groupId", description = "그룹 아이디")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "422", description = "존재하지 않은 그룹이거나 사용자의 그룹이 아닙니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            })
    ResponseEntity<Void> update(@Parameter(hidden = true) Long userId, Long groupId, GroupUpdateRequest request);
}
