package com.map.gaja.user.presentation.api.specification;

import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.user.presentation.dto.request.LoginRequest;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

public interface UserApiSpecification {

    @Operation(summary = "로그인 요청",
            description = "카카오 액세스토큰을 이용한 로그인",
            responses = {
                    @ApiResponse(responseCode = "200", headers = {@Header(name = "Set-Cookie", description = "인증 토큰 ex) JSESSIONID =1231231232d", schema = @Schema(implementation = String.class))}, description = "최근에 참조한 번들아이디 => null이면 클라이언트 조회 api 호출X / null이 아닌 정수면 클라이언트 조회", content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    ResponseEntity<Long> login(LoginRequest request);

    @Operation(summary = "로그아웃 요청",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공하면 앱에 저장된 토큰 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    ResponseEntity<Void> logout(HttpSession session);

}
