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
//
//import javax.persistence.EntityManager;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class ClientImageRepositoryTest extends NativeRepositoryTest{
//    @Autowired
//    ClientImageRepository clientImageRepository;
//
//    @Autowired
//    EntityManager em;
//
//    @Test
//    @DisplayName("삭제된 그룹에 대한 클라이언트 이미지 필드인 isDeleted update")
//    void markDeleted() {
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
//        for (int j = 0; j < 5; j++) {
//            Client client = new Client("name", "010-1234-1234", group);
//            client.updateImage(new ClientImage("qwer", "qwer"));
//            em.persist(client);
//        }
//
//        em.flush();
//        em.clear();
//
//        //when
//        int result = clientImageRepository.markDeleted();
//
//        assertEquals(5, result);
//    }
//}