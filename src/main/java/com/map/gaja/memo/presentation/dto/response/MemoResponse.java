package com.map.gaja.memo.presentation.dto.response;

import com.map.gaja.memo.domain.model.Memo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoResponse {
    private Long memoId;
    private String memoType;
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
