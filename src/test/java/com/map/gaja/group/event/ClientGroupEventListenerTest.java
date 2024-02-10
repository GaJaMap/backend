package com.map.gaja.group.event;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.event.ClientGroupUpdatedEvent;
import com.map.gaja.client.event.GroupClientAddedEvent;
import com.map.gaja.group.domain.exception.GroupNotFoundException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.domain.service.IncreasingClientService;
import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientGroupEventListenerTest {
    @Mock IncreasingClientService increasingClientService;
    @Mock GroupRepository groupRepository;
    @InjectMocks
    ClientGroupEventListener eventListener;

    private User mockUser;
    private Long groupId = 1L;
    private Group mockGroup;
    private int beforeClientCount = 1;

    @BeforeEach
    void beforeEach() {
        mockUser = TestEntityCreator.createUser("TestUser@Example.com");
        mockGroup = TestEntityCreator.createGroup(mockUser, groupId, "TestGroup", beforeClientCount);
    }

    @Test
    @DisplayName("증가 로직 검증")
    void increaseClientCount() {
        GroupClientAddedEvent event = new GroupClientAddedEvent(groupId, mockUser);
        when(groupRepository.findGroupByIdForUpdate(groupId)).thenReturn(Optional.of(mockGroup));
        eventListener.increaseClientCount(event);

        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(increasingClientService).increaseByOne(mockGroup, mockUser.getAuthority());
    }

    @Test
    @DisplayName("찾을 수 없는 Group으로 증가 실패 로직")
    void increaseClientCountFail() {
        GroupClientAddedEvent event = new GroupClientAddedEvent(groupId, mockUser);
        when(groupRepository.findGroupByIdForUpdate(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> eventListener.increaseClientCount(event));

        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(increasingClientService, times(0)).increaseByOne(any(), any());
    }

    @Test
    @DisplayName("변경 성공")
    void changeGroup() {
        Long otherGroupId = 2L;
        Group otherMockGroup = mock(Group.class);
        ClientGroupUpdatedEvent event = new ClientGroupUpdatedEvent(groupId, otherGroupId, mockUser);
        when(groupRepository.findGroupByIdForUpdate(groupId)).thenReturn(Optional.of(mockGroup));
        when(groupRepository.findGroupByIdForUpdate(otherGroupId)).thenReturn(Optional.of(otherMockGroup));

        eventListener.changeGroup(event);

        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(groupRepository).findGroupByIdForUpdate(otherGroupId);
        verify(increasingClientService).increaseByOne(otherMockGroup, mockUser.getAuthority());
        assertThat(otherMockGroup.getClientCount()).isEqualTo(beforeClientCount - 1);
    }

    @Test
    @DisplayName("같은 GroupId가 들어옴.")
    void changeGroupFail() {
        Long sameGroupId = groupId;
        ClientGroupUpdatedEvent event = new ClientGroupUpdatedEvent(groupId, sameGroupId, mockUser);

        assertThrows(IllegalArgumentException.class, () -> eventListener.changeGroup(event));
    }

    @Test
    @DisplayName("preGroup 찾을 수 없음")
    void changeGroupFail1() {
        Long otherGroupId = 2L;
        Group otherMockGroup = mock(Group.class);
        ClientGroupUpdatedEvent event = new ClientGroupUpdatedEvent(groupId, otherGroupId, mockUser);
        when(groupRepository.findGroupByIdForUpdate(groupId)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> eventListener.changeGroup(event));

        verify(groupRepository).findGroupByIdForUpdate(groupId);
    }

    @Test
    @DisplayName("changedGroup 찾을 수 없음")
    void changeGroupFail2() {
        Long otherGroupId = 2L;
        ClientGroupUpdatedEvent event = new ClientGroupUpdatedEvent(groupId, otherGroupId, mockUser);
        when(groupRepository.findGroupByIdForUpdate(groupId)).thenReturn(Optional.of(mockGroup));
        when(groupRepository.findGroupByIdForUpdate(otherGroupId)).thenReturn(Optional.empty());


        assertThrows(GroupNotFoundException.class, () -> eventListener.changeGroup(event));

        verify(groupRepository).findGroupByIdForUpdate(groupId);
        verify(groupRepository).findGroupByIdForUpdate(otherGroupId);
    }
}