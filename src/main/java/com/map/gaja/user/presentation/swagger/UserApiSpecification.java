package com.map.gaja.user.presentation.swagger;

import com.map.gaja.global.exception.ExceptionDto;
import com.map.gaja.user.presentation.dto.request.Req;
import com.map.gaja.user.presentation.dto.response.Res;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
}
