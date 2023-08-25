//package com.map.gaja.global.schedule;
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
//import org.springframework.test.annotation.Rollback;
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
//class DeletionTaskTest {
//    @Autowired
//    DeletionTask deletionTask;
//
//    @Autowired
//    EntityManager em;
//
//
//    /**
//     * 유저 2명에 각 유저당 그룹50개, 그룹당 클라이언트와 클라이언트 이미지 100개씩 총 10000개의 데이터
//     * DB에서의 삭제작업은 0.5초 소요
//     * S3 삭제작업은 3분 소요
//     */
//
//    @Test
//    @Rollback(false)
//    @DisplayName("유저, 그룹, 클라이언트 삭제 작업")
//    void deleteScheduling() {
//
//        for(int i=0;i<2;i++){
//            User user = User.builder()
//                    .email("test"+i)
//                    .groupCount(0)
//                    .authority(Authority.FREE)
//                    .lastLoginDate(LocalDateTime.now())
//                    .active(false)
//                    .build();
//            em.persist(user);
//
//            for(int g=0;g<50;g++){
//                Group group = new Group(user.getEmail(), user);
//                group.remove();
//                em.persist(group);
//
//                for (int c = 0; c < 100; c++) {
//                    Client client = new Client("name", "010-1234-1234", group);
//                    client.updateImage(new ClientImage("qwer", "qwer"));
//                    em.persist(client);
//                }
//            }
//        }
//        em.flush();
//        em.clear();
//
//        assertDoesNotThrow(() -> deletionTask.execute());
//    }
//}