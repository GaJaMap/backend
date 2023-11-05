//package com.map.gaja.client.infrastructure.repository;
//
//import com.map.gaja.client.domain.model.Client;
//import com.map.gaja.client.domain.model.ClientImage;
//import com.map.gaja.group.domain.model.Group;
//import com.map.gaja.user.domain.model.Authority;
//import com.map.gaja.user.domain.model.User;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.EntityManager;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class ClientRepositoryTest {
//    @Autowired
//    ClientRepository clientRepository;
//
//    @Autowired
//    EntityManager em;
//
//    @Test
//    @DisplayName("삭제된 그룹에 속한 클라이언트 전부 삭제")
//    void deleteClientsInDeletedGroup() {
//        //given
//        User user = User.builder()
//                .email("test")
//                .groupCount(0)
//                .authority(Authority.FREE)
//                .lastLoginDate(LocalDateTime.now())
//                .active(false)
//                .build();
//        em.persist(user);
//
//        Group group = new Group(user.getEmail(), user);
//        group.remove();
//        em.persist(group);
//
//        User user2 = User.builder()
//                .email("test2")
//                .groupCount(0)
//                .authority(Authority.FREE)
//                .lastLoginDate(LocalDateTime.now())
//                .active(true)
//                .build();
//        em.persist(user2);
//
//        Group group2 = new Group(user2.getEmail(), user2);
//        em.persist(group2);
//
//        for (int j = 0; j < 5; j++) {
//            Client client = new Client("name", "010-1234-1234", group);
//            em.persist(client);
//        }
//
//        for (int j = 0; j < 5; j++) {
//            Client client = new Client("name", "010-1234-1234", group2);
//            em.persist(client);
//        }
//
//        em.flush();
//        em.clear();
//
//        //when
//        int result = clientRepository.deleteClientsInDeletedGroup();
//
//        assertEquals(5, result);
//    }
//}