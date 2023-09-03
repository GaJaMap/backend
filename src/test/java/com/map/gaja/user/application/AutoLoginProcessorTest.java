package com.map.gaja.user.application;

import com.map.gaja.client.apllication.ClientQueryService;
import com.map.gaja.client.infrastructure.s3.S3UrlGenerator;
import com.map.gaja.client.presentation.dto.response.ClientListResponse;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoLoginProcessorTest {
    @InjectMocks
    AutoLoginProcessor autoLoginProcessor;

    @Mock
    GroupRepository groupRepository;

    @Mock
    ClientQueryService clientQueryService;

    @Mock
    UserRepository userRepository;

    @Mock
    S3UrlGenerator s3UrlGenerator;

    @Test
    @DisplayName("사용자가 최근에 참조한 전체 그룹 조회")
    void findWholeGroup() {
        String email = "test@gmail.com";
        User user = new User(email);
        ClientListResponse clientListResponse = new ClientListResponse();

        when(userRepository.findByEmailAndActive(email)).thenReturn(Optional.of(user));
        when(clientQueryService.findAllClient(email, null)).thenReturn(clientListResponse);
        autoLoginProcessor.process(email);

        verify(clientQueryService, times(1)).findAllClient(email, null);
    }

    @Test
    @DisplayName("사용자가 최근에 참조한 특정 그룹 조회")
    void findGroup() {
        String email = "test@gmail.com";
        User user = new User(email);
        user.accessGroup(1L);
        ClientListResponse clientListResponse = new ClientListResponse();
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

        when(userRepository.findByEmailAndActive(email)).thenReturn(Optional.of(user));
        when(groupRepository.findGroupInfoById(user.getReferenceGroupId())).thenReturn(Optional.of(groupInfo));
        when(clientQueryService.findAllClientsInGroup(groupInfo.getGroupId(), null)).thenReturn(clientListResponse);
        autoLoginProcessor.process(email);

        verify(clientQueryService, times(1)).findAllClientsInGroup(groupInfo.getGroupId(), null);
        verify(groupRepository, times(1)).findGroupInfoById(user.getReferenceGroupId());
    }
}