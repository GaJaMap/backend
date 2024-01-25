package com.map.gaja.memo.application;

import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;

import com.map.gaja.memo.domain.model.Memo;
import com.map.gaja.memo.domain.model.MemoType;
import com.map.gaja.memo.infrastructure.MemoRepository;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;
import com.map.gaja.memo.presentation.dto.response.MemoPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemoService {
    private final MemoRepository memoRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public Long create(Long userId, Long clientId, MemoCreateRequest request) {
        Client client = clientRepository.findByIdAndUser(clientId, userId)
                .orElseThrow(ClientNotFoundException::new);

        Memo memo = new Memo(request.getMessage(), MemoType.from(request.getMemoType()), client, client.getUser());
        memoRepository.save(memo);

        return memo.getId();
    }

    @Transactional
    public void delete(Long userId, Long memoId) {
        memoRepository.deleteByIdAndUser(memoId, userId);
    }

    @Transactional(readOnly = true)
    public MemoPageResponse findPageByClientId(Long userId, Long clientId, Pageable pageable) {
        Slice<Memo> memos = memoRepository.findPageByClientId(clientId, userId, pageable);

        return MemoPageResponse.from(memos);
    }
}
