package com.map.gaja.user.application;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.infrastructure.s3.S3UrlGenerator;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.client.presentation.dto.response.ClientOverviewResponse;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import com.map.gaja.user.presentation.dto.ReferenceGroupId;
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

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("사용자가 최근에 참조한 전체 그룹 아이디 조회")
    void findReferenceWholeGroupId() {
        // given
        String email = "test@gmail.com";
        User user = new User(email);
        ReferenceGroupId referenceGroupId = new ReferenceGroupId(null);

        given(userRepository.findByEmailAndActive(email))
                .willReturn(Optional.of(user));

        // when
        ReferenceGroupId result = autoLoginProcessor.login(email);

        // then
        assertThat(referenceGroupId.getId()).isEqualTo(result.getId());
    }

    @Test
    @DisplayName("사용자가 최근에 참조한 특정 그룹 아이디 조회")
    void findReferenceGroupId() {
        // given
        String email = "test@gmail.com";
        User user = User.builder()
                .referenceGroupId(1L)
                .lastLoginDate(LocalDateTime.now())
                .build();
        ReferenceGroupId referenceGroupId = new ReferenceGroupId(1L);

        given(userRepository.findByEmailAndActive(email))
                .willReturn(Optional.of(user));

        // when
        ReferenceGroupId result = autoLoginProcessor.login(email);

        // then
        assertThat(referenceGroupId.getId()).isEqualTo(result.getId());
    }

    @Test
    @DisplayName("사용자가 최근에 참조한 전체 그룹 조회")
    void findWholeGroup() {
        // given
        String email = "test@gmail.com";
        User user = new User(email);
        List<ClientOverviewResponse> clientList = new ArrayList<>();
        ReferenceGroupId referenceGroupId = new ReferenceGroupId(null);

        when(clientQueryRepository.findActiveClientByEmail(email, null)).thenReturn(clientList);

        // when
        autoLoginProcessor.findReferenceGroupInClients(email, referenceGroupId);

        // then
        verify(clientQueryRepository).findActiveClientByEmail(email, null);
    }

    @Test
    @DisplayName("사용자가 최근에 참조한 특정 그룹 조회")
    void findGroup() {
        // given
        String email = "test@gmail.com";
        User user = new User(email);
        user.accessGroup(1L);
        ReferenceGroupId referenceGroupId = new ReferenceGroupId(1L);
        List<Client> clients = new ArrayList<>();
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

        when(groupRepository.findGroupInfoById(referenceGroupId.getId())).thenReturn(Optional.of(groupInfo));
        when(clientQueryRepository.findByGroup_Id(referenceGroupId.getId(), null)).thenReturn(clients);

        // when
        autoLoginProcessor.findReferenceGroupInClients(email, referenceGroupId);

        // then
        verify(clientQueryRepository).findByGroup_Id(referenceGroupId.getId(), null);
        verify(groupRepository).findGroupInfoById(user.getReferenceGroupId());
    }
}