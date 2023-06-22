package com.map.gaja.user.presentation.api.specification;

import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.user.presentation.dto.request.LoginRequest;
import com.map.gaja.user.presentation.dto.request.Req;
import com.map.gaja.user.presentation.dto.response.Res;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface UserApiSpecification {
    @Operation(summary = "api 설명에 대한 간단 요약",
            description = "세부사항",
            //parameters = {@Parameter(name = "token", description = "액세스 토큰")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = Res.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    Res test(Req req);

    @Operation(summary = "로그인 요청",
            description = "카카오 액세스토큰을 이용한 로그인",
            responses = {
                    @ApiResponse(responseCode = "200", description = "최근에 참조한 번들아이디 => null이면 클라이언트 조회 api 호출X / null이 아닌 정수면 클라이언트 조회", content = @Content(schema = @Schema(implementation = Integer.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    ResponseEntity<Integer> login(LoginRequest request);
}
