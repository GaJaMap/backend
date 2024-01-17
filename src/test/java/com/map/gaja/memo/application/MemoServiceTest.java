package com.map.gaja.memo.application;

import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.memo.infrastructure.MemoRepository;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemoServiceTest {
    @Mock
    ClientRepository clientRepository;

    @Mock
    MemoRepository memoRepository;

    @InjectMocks
    MemoService memoService;

    @Test
    @DisplayName("메모 생성에 성공한다.")
    void createSuccess() {
        // given
        Long userId = 1L;
        Long clientId = 1L;
        MemoCreateRequest request = new MemoCreateRequest("message", "CALL");
        given(clientRepository.findByIdAndUser(anyLong(), anyLong()))
                .willReturn(Optional.of(new Client("a", "a", null, null)));

        // when
        memoService.create(userId, clientId, request);

        // when, then
        verify(memoRepository).save(any());
    }

    @Test
    @DisplayName("Client 조회 실패로 메모 생성에 실패한다.")
    void createFail() {
        // given
        Long userId = 1L;
        Long clientId = 1L;
        MemoCreateRequest request = new MemoCreateRequest("message", "CALL");
        given(clientRepository.findByIdAndUser(anyLong(), anyLong()))
                .willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> memoService.create(userId, clientId, request))
                .isInstanceOf(ClientNotFoundException.class);


    }
}