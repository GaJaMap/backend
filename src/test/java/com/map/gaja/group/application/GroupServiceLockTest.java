//package com.map.gaja.group.application;
//
//import com.map.gaja.group.infrastructure.GroupRepository;
//import com.map.gaja.group.presentation.dto.request.GroupCreateRequest;
//import com.map.gaja.user.domain.model.Authority;
//import com.map.gaja.user.domain.model.User;
//import com.map.gaja.user.infrastructure.UserRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//
//@SpringBootTest
//public class GroupServiceLockTest {
//    @Autowired
//    GroupRepository groupRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    GroupService groupService;
//
//    @BeforeEach
//    void init() {
//        User user = User.builder()
//                .email("test")
//                .groupCount(0)
//                .active(true)
//                .authority(Authority.VIP)
//                .lastLoginDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
//                .build();
//        userRepository.save(user);
//    }
//
//    @Test
//    @DisplayName("그룹 생성시 비관적 락 적용 테스트")
//    void create() throws InterruptedException {
//        //given
//        int expected = 100;
//        User user = userRepository.findByEmail("test");
//
//        //when
//        Thread[] threads = new Thread[expected];
//        for (int i = 0; i < expected; i++) {
//            GroupCreateRequest groupCreateRequest = new GroupCreateRequest("group" + i);
//
//            threads[i] = new Thread(() -> {
//                groupService.create(user.getId(), groupCreateRequest);
//            });
//            threads[i].start();
//        }
//
//        for(Thread thread : threads) {
//            thread.join();
//        }
//
//        //then
//        User u = userRepository.findByEmail("test");
//        Assertions.assertEquals(expected, u.getGroupCount());
//    }
//
//}
