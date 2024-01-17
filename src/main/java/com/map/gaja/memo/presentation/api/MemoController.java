package com.map.gaja.memo.presentation.api;

import com.map.gaja.memo.application.MemoService;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/memo")
@RequiredArgsConstructor
public class MemoController {
    private final MemoService memoService;

    @PostMapping("/client/{clientId}")
    public ResponseEntity<Void> create(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @PathVariable Long clientId,
            @Valid @RequestBody MemoCreateRequest memoCreateRequest
    ) {
        memoService.create(userId, clientId, memoCreateRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
