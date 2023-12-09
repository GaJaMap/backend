package com.map.gaja.client.application;

import com.map.gaja.client.domain.exception.ClientNotFoundException;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.presentation.dto.access.ClientAccessCheckDto;
import com.map.gaja.client.presentation.dto.access.ClientListAccessCheckDto;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientAccessVerifyServiceTest {

    @Mock ClientQueryRepository clientQueryRepository;
    @Mock GroupAccessVerifyService groupAccessVerifyService;

    @InjectMocks
    ClientAccessVerifyService clientAccessVerifyService;

    final String email = "test@example.com";
    final long clientId = 1;
    final long groupId = 1;
    final List<Long> clientIds = List.of(1L,2L,3L);


    @Test
    @DisplayName("단일 Client 접근 검증 성공")
    @Tag("Single")
    void verifyClientAccessTest() {
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, groupId, clientId);
        doNothing().when(groupAccessVerifyService).verifyGroupAccess(groupId, email);
        when(clientQueryRepository.hasNoClientByGroup(groupId, clientId)).thenReturn(false);

        clientAccessVerifyService.verifyClientAccess(accessRequest);
    }

    @Test
    @DisplayName("단일 잘못된 ClientId 검증")
    @Tag("Single")
    void verifyFailClientIdTest() {
        long differentGroupClientId = -1;
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, groupId, differentGroupClientId);
        doNothing().when(groupAccessVerifyService).verifyGroupAccess(groupId, email);
        when(clientQueryRepository.hasNoClientByGroup(groupId, differentGroupClientId)).thenReturn(true);

        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientAccessVerifyService.verifyClientAccess(accessRequest));
    }

    @Test
    @DisplayName("단일 잘못된 GroupId 찾기 검증")
    @Tag("Single")
    void verifyFailGroupTest() {
        long differentGroupId = -1;
        ClientAccessCheckDto accessRequest = new ClientAccessCheckDto(email, differentGroupId, clientId);
        doThrow(GroupNotFoundException.class).when(groupAccessVerifyService).verifyGroupAccess(differentGroupId, email);

        Assertions.assertThrows(GroupNotFoundException.class ,
                () -> clientAccessVerifyService.verifyClientAccess(accessRequest));
    }

    @Test
    @DisplayName("Multi - 접근 검증")
    void verifyClientListAccessTest() {
        ClientListAccessCheckDto accessRequest = new ClientListAccessCheckDto(email, groupId, clientIds);
        doNothing().when(groupAccessVerifyService).verifyGroupAccess(groupId, email);
        when(clientQueryRepository.findMatchingClientCountInGroup(groupId, clientIds)).thenReturn(Long.valueOf(clientIds.size()));

        clientAccessVerifyService.verifyClientListAccess(accessRequest);
    }

    @Test
    @DisplayName("Multi - 검색 시 DB에 없는 ID가 있음")
    void verifyListFailClientIdTest() {
        long wrongClientIdCount = 1;
        ClientListAccessCheckDto accessRequest = new ClientListAccessCheckDto(email, groupId, clientIds);
        doNothing().when(groupAccessVerifyService).verifyGroupAccess(groupId, email);
        when(clientQueryRepository.findMatchingClientCountInGroup(groupId, clientIds)).thenReturn(Long.valueOf(clientIds.size()-wrongClientIdCount));
        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientAccessVerifyService.verifyClientListAccess(accessRequest));
    }

    @Test
    @DisplayName("Multi - 잘못된 GroupId 검증")
    void verifyListFailGroupIdTest() {
        long wrongGroupId = -1L;
        ClientListAccessCheckDto accessRequest = new ClientListAccessCheckDto(email, wrongGroupId, clientIds);
        doThrow(GroupNotFoundException.class).when(groupAccessVerifyService).verifyGroupAccess(wrongGroupId, email);
        Assertions.assertThrows(GroupNotFoundException.class,
                () -> clientAccessVerifyService.verifyClientListAccess(accessRequest));
    }
}