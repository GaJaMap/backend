package com.map.gaja.memo.presentation.dto.response;

import com.map.gaja.memo.domain.model.Memo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoResponse {
    @Schema(description = "메모 아이디")
    private Long memoId;

    @Schema(description = "메모 타입은 영어로 CALL(전화), NAVIGATION(내비), MESSAGE(메시지)")
    private String memoType;

    @Schema(description = "메모 타입이 MESSAGE이면 내용이 있고 다른 타입은 NULL")
    private String message;

    private MemoResponse(Long memoId, String memoType, String message) {
        this.memoId = memoId;
        this.memoType = memoType;
        this.message = message;
    }

    public static MemoResponse from(Memo memo) {
        return new MemoResponse(memo.getId(), memo.getMemoType().name(), memo.getMessage());
    }
}
