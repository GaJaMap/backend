package com.map.gaja.group.infrastructure;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.group.presentation.dto.response.GroupInfo;
import com.map.gaja.user.domain.model.Authority;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GroupRepositoryTest {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("그룹 조회 쿼리 테스트")
    void findGroup() {
        //given
        User user = User.builder()
                .email("test")
                .groupCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        for (int i = 0; i < 11; i++) {
            Group group = Group.builder()
                    .name("test")
                    .clientCount(0)
                    .user(user)
                    .createdDate(LocalDateTime.now())
                    .build();
            groupRepository.save(group);
        }
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Slice<GroupInfo> groupInfos = groupRepository.findGroupByUserId(user.getId(), pageable);

        //then
        assertEquals(true, groupInfos.hasNext());
        assertEquals(10, groupInfos.getContent().size());
        assertEquals("test", groupInfos.getContent().get(1).getGroupName());

    }

    @Test
    @DisplayName("그룹 삭제 성공")
    void deleteGroup() {
        //given
        User user = User.builder()
                .email("test")
                .groupCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Group group = Group.builder()
                .name("test")
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        groupRepository.save(group);

        //when
        int count = groupRepository.deleteByIdAndUserId(group.getId(), user.getId());

        //then
        assertEquals(1, count);
    }

    @Test
    @DisplayName("그룹 조회 성공")
    void find() {
        //given
        User user = User.builder()
                .email("test")
                .groupCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Group group = Group.builder()
                .name("test")
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        groupRepository.save(group);

        //when & then
        assertEquals(group, groupRepository.findByIdAndUserId(group.getId(), user.getId()).get());
    }

    @Test
    @DisplayName("groupId와 email로 Group 조회")
    void findByIdAndUserEmailTest() {
        //given
        User user = User.builder()
                .email("test")
                .groupCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Group group = Group.builder()
                .name("test")
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        groupRepository.save(group);

        Group result = groupRepository.findByIdAndUserEmail(group.getId(), user.getEmail())
                .orElseThrow(IllegalArgumentException::new);

        assertEquals(group, result);
    }
}