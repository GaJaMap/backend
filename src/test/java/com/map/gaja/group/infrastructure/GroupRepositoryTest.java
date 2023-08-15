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
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        for (int i = 0; i < 11; i++) {
            Group group = new Group("test", user);
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
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Group group = new Group("test", user);
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
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Group group = new Group("test", user);
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
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Group group = new Group("test", user);
        groupRepository.save(group);

        Group result = groupRepository.findByIdAndUserEmail(group.getId(), user.getEmail())
                .orElseThrow(IllegalArgumentException::new);

        assertEquals(group, result);
    }

    @Test
    @DisplayName("회원 탈퇴한 유저들 그룹 필드인 isDeleted true로 업데이트")
    void deleteByWithdrawalUser() {
        //given
        User user = new User("test");
        userRepository.save(user);

        for(int i=0;i<5;i++){
            User u = User.builder()
                    .email("test"+i)
                    .groupCount(0)
                    .authority(Authority.FREE)
                    .lastLoginDate(LocalDateTime.now())
                    .active(false)
                    .build();
            userRepository.save(u);

            Group group = new Group(u.getEmail(), u);
            groupRepository.save(group);
        }

        //when
        int result = groupRepository.deleteByWithdrawalUser();

        //then
        assertEquals(5, result);
    }

    @Test
    @DisplayName("isDeleted true인 group들 전부 삭제")
    void deleteMarkedGroups() {
        //given
        User user = new User("test");
        userRepository.save(user);

        for(int i=0;i<5;i++){
            Group group = new Group(user.getEmail(), user); //삭제된 그룹
            group.remove();
            groupRepository.save(group);
        }

        Group group = new Group(user.getEmail(), user); //삭제안된 그룹
        groupRepository.save(group);

        //when
        int result = groupRepository.deleteMarkedGroups();

        //then
        assertEquals(5, result);
    }

    @Test
    @DisplayName("사용자가 최근에 참조한 그룹 정보 조회")
    void findGroupInfo() {
        //given
        User user = new User("test");
        userRepository.save(user);

        Group group = new Group("name", user);
        groupRepository.save(group);
        user.accessGroup(group.getId());

        //when
        GroupInfo groupInfo = groupRepository.findGroupInfoById(group.getId());

        //then
        assertEquals(group.getId(), groupInfo.getGroupId());
        assertEquals("name", groupInfo.getGroupName());
        assertEquals(0, groupInfo.getClientCount());
    }

    @Test
    @DisplayName("사용자가 최근에 참조한 그룹 정보 조회 null 테스트")
    void findGroupInfoNull() {
        //given
        User user = new User("test");
        userRepository.save(user);

        //when
        GroupInfo groupInfo = groupRepository.findGroupInfoById(0L);

        //then
        assertEquals(null, groupInfo);
    }
}