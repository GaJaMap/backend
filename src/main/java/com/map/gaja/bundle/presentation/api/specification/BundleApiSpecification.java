package com.map.gaja.bundle.presentation.api.specification;

import com.map.gaja.bundle.presentation.dto.request.BundleCreateRequest;
import com.map.gaja.bundle.presentation.dto.response.BundleLimitExceededResponse;
import com.map.gaja.bundle.presentation.dto.response.BundleResponse;
import com.map.gaja.bundle.presentation.dto.response.DeletionFailedResponse;
import com.map.gaja.user.presentation.dto.response.NotFoundResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BundleApiSpecification {
    @Operation(summary = "번들 생성",
            parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "204", description = "성공"),
                    @ApiResponse(responseCode = "403", description = "권한에 따른 번들 생성 초과", content = @Content(schema = @Schema(implementation = BundleLimitExceededResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = NotFoundResponse.class)))
            })
    ResponseEntity<Void> create(
            String email,
            BundleCreateRequest request
    );

    @Operation(summary = "번들 조회",
            parameters = {
                    @Parameter(name = "token", description = "액세스 토큰"),
                    @Parameter(name = "page", description = "페이지 번호 0부터 시작")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = BundleResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = NotFoundResponse.class)))
            })
    ResponseEntity<BundleResponse> read(@Parameter(hidden = true) String email, @Parameter(hidden = true) Pageable pageable);

    @Operation(summary = "번들 삭제",
            parameters = {
                    @Parameter(name = "token", description = "액세스 토큰"),
                    @Parameter(name = "bundleId", description = "번들 아이디")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = BundleResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = NotFoundResponse.class))),
                    @ApiResponse(responseCode = "422", description = "존재하지 않은 번들이거나 사용자의 번들이 아닙니다.", content = @Content(schema = @Schema(implementation = DeletionFailedResponse.class)))
            })
    ResponseEntity<Void> delete(@Parameter(hidden = true) String email, Long bundleId);
}
