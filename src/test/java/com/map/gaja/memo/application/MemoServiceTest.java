package com.map.gaja.memo.application;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.memo.domain.exception.MemoNotFoundException;
import com.map.gaja.memo.domain.model.Memo;
import com.map.gaja.memo.domain.model.MemoType;
import com.map.gaja.memo.infrastructure.MemoRepository;
import com.map.gaja.memo.presentation.dto.request.MemoCreateRequest;
import com.map.gaja.memo.presentation.dto.response.MemoPageResponse;
import com.map.gaja.memo.presentation.dto.response.MemoResponse;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
        User testUser = TestEntityCreator.createUser("test@email.com");
        given(clientRepository.findByIdAndUser(anyLong(), anyLong()))
                .willReturn(Optional.of(mock(Client.class)));

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

    @Test
    @DisplayName("메모를 최신순으로 조회한다.")
    void findPageByClientId() {
        // given
        Memo memo = new Memo(null, MemoType.CALL, null, null);
        Memo memo2 = new Memo(null, MemoType.NAVIGATION, null, null);
        List<Memo> memos = List.of(memo, memo2);
        Long userId = 1L;
        Long clientId = 1L;
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Order.desc("id")));
        List<MemoResponse> memoResponses = List.of(MemoResponse.from(memo), MemoResponse.from(memo2));

        given(memoRepository.findPageByClientId(userId, clientId, pageable))
                .willReturn(new SliceImpl<>(memos, pageable, true));

        // when
        MemoPageResponse expect = memoService.findPageByClientId(userId, clientId, pageable);

        // then
        assertThat(expect.getMemos())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyElementsOf(memoResponses);
    }
}