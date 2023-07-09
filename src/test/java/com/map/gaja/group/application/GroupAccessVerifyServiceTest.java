package com.map.gaja.group.application;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.infrastructure.GroupQueryRepository;
import com.map.gaja.user.domain.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupAccessVerifyServiceTest {

    @Mock
    GroupQueryRepository groupQueryRepository;

    @InjectMocks
    GroupAccessVerifyService groupAccessVerifyService;
    @Test
    @DisplayName("그룹 접근 확인")
    void verifyGroupAccess() {
        Long groupId = 2L;
        String email = "test@example.com";
        User user = User.builder()
                .id(1L).email(email).referenceGroupId(null).build();
        Group group = Group.builder()
                .id(groupId).user(user).build();
        when(groupQueryRepository.findGroupByUser(anyLong(), anyString()))
                .thenReturn(Optional.ofNullable(group));

        groupAccessVerifyService.verifyGroupAccess(groupId, email);

        assertThat(user.getReferenceGroupId()).isEqualTo(groupId);
    }
}