package com.map.gaja.memo.application;

import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.memo.domain.model.Memo;
import com.map.gaja.memo.domain.model.MemoType;
import com.map.gaja.memo.infrastructure.MemoRepository;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;
import lombok.RequiredArgsConstructor;
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
                .orElseThrow(() -> {
                    throw new ClientNotFoundException();
                });

        Memo memo = new Memo(request.getMessage(), MemoType.from(request.getMemoType()), client);
        memoRepository.save(memo);

        return memo.getId();
    }
}
