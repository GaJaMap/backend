package com.map.gaja.memo.presentation.dto.response;

import com.map.gaja.memo.domain.model.Memo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoPageResponse {
    private boolean hasNext;
    private List<MemoResponse> memos;

    private MemoPageResponse(boolean hasNext, List<MemoResponse> memos) {
        this.hasNext = hasNext;
        this.memos = memos;
    }

    public static MemoPageResponse from(Slice<Memo> page) {
        List<MemoResponse> memos = page.getContent().stream()
                .map(MemoResponse::from)
                .collect(Collectors.toList());

        return new MemoPageResponse(page.hasNext(), memos);
    }

}
