package com.map.gaja.user.application;

import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.s3.S3UrlGenerator;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoLoginProcessorTest {
    @InjectMocks
    AutoLoginProcessor autoLoginProcessor;

    @Mock
    GroupRepository groupRepository;

    @Mock
    ClientQueryRepository clientQueryRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    S3UrlGenerator s3UrlGenerator;


    @Test
    @DisplayName("사용자가 최근에 참조한 전체 그룹 조회")
    void findWholeGroup() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .lastLoginDate(LocalDateTime.now())
                .authority(Authority.FREE)
                .build();
        List<ClientOverviewResponse> clientList = new ArrayList<>();

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(clientQueryRepository.findWholeGroupClients(anyLong(), anyInt()))
                .willReturn(clientList);

        // when
        autoLoginProcessor.process(userId);

        // then
        verify(clientQueryRepository).findWholeGroupClients(userId, user.getAuthority().getClientLimitCount());
    }

    @Test
    @DisplayName("사용자가 최근에 참조한 특정 그룹 조회")
    void findGroup() {
        // given
        Long userId = 1L;
        User user = new User("test@gmail.com");
        user.accessGroup(1L);
        List<ClientOverviewResponse> clients = new ArrayList<>();
        GroupInfo groupInfo = new GroupInfo() {
            @Override
            public Long getGroupId() {
                return 1L;
            }

            @Override
            public String getGroupName() {
                return null;
            }

            @Override
            public Integer getClientCount() {
                return null;
            }
        };

        given(userRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(groupRepository.findGroupInfoById(anyLong()))
                .willReturn(Optional.of(groupInfo));
        given(clientQueryRepository.findRecentGroupClients(anyLong()))
                .willReturn(clients);

        // when
        autoLoginProcessor.process(userId);

        // then
        verify(clientQueryRepository).findRecentGroupClients(user.getReferenceGroupId());
        verify(groupRepository).findGroupInfoById(user.getReferenceGroupId());
    }
}