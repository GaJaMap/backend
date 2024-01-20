package com.map.gaja.memo.presentation.api;

import com.map.gaja.memo.application.MemoService;
import com.map.gaja.memo.presentation.api.specification.MemoApiSpecification;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;
import com.map.gaja.memo.presentation.dto.response.MemoPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/memo")
@RequiredArgsConstructor
public class MemoController implements MemoApiSpecification {
    private final MemoService memoService;

    @PostMapping("/client/{clientId}")
    public ResponseEntity<Long> create(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long clientId,
            @Valid @RequestBody MemoCreateRequest memoCreateRequest
    ) {
        Long memoId = memoService.create(userId, clientId, memoCreateRequest);
        return new ResponseEntity<>(memoId, HttpStatus.CREATED);
    }

    @DeleteMapping("{memoId}/client/{clientId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long memoId,
            @PathVariable Long clientId
    ) {
        memoService.delete(userId, clientId, memoId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<MemoPageResponse> read(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long clientId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        MemoPageResponse response = memoService.findPageByClientId(userId, clientId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
