package com.map.gaja.user.presentation.api.specification;

import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.user.presentation.dto.request.LoginRequest;

import com.map.gaja.user.presentation.dto.response.AutoLoginResponse;
import com.map.gaja.user.presentation.dto.response.LoginResponse;
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
                    @ApiResponse(responseCode = "200", headers = {@Header(name = "Set-Cookie", description = "인증 토큰 ex) JSESSIONID =1231231232d", schema = @Schema(implementation = String.class))}, content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    ResponseEntity<LoginResponse> login(LoginRequest request);

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

    @Operation(summary = "회원 탈퇴 요청",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공하면 앱에 저장된 토큰 삭제"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    ResponseEntity<Void> withdrawal(@Parameter(hidden = true) String email, HttpSession session);

    @Operation(summary = "앱 자동로그인 처리(앱 실행할 때마다 요청)",
            parameters = {
                    @Parameter(name = "JSESSIONID", description = "세션 ID", in = ParameterIn.HEADER),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자가 최근에 참조한 그룹정보와, 그룹에 속한 고객들 응답"),
                    @ApiResponse(responseCode = "403", description = "권한이 없음(세션이 만료되었거나 로그인처리가 안된 사용자)", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    ResponseEntity<AutoLoginResponse> autoLogin(@Parameter(hidden = true) String email);

}
