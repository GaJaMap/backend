package com.map.gaja.memo.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoCreateRequest {
    @Schema(description = "메모 메시지")
    @NotNull(message = "메세지를 입력해 주세요.")
    @Size(max = 100, message = "메시지는 100자 이하로 입력해 주세요.")
    private String message;

    @Schema(description = "메모 타입은 영어로 CALL(전화), NAVIGATION(내비), MESSAGE(메시지)")
    @NotNull(message = "타입을 지정해 주세요.")
    @Size(max = 15)
    private String memoType;


    public MemoCreateRequest(String message, String memoType) {
        this.message = message;
        this.memoType = memoType;
    }
}
