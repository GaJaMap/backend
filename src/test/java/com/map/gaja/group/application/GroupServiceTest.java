package com.map.gaja.group.application;

import com.map.gaja.group.infrastructure.GroupRepository;
import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    GroupRepository groupRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    GroupService groupService;

    @Test
    void 그룹_생성_실패() {
        String email="test@gmail.com";
        GroupCreateRequest groupCreateRequest = new GroupCreateRequest("bundle");
        User user = User.builder()
                .id(1L)
                .email(email)
                .groupCount(100)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThatThrownBy(()-> groupService.create(email, groupCreateRequest)).isInstanceOf(GroupLimitExceededException.class);
    }

}