package com.map.gaja.client.application;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.infrastructure.repository.ClientBulkRepository;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.group.domain.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * 각각의 성능을 보기 위한 테스트 코드
 * 성능 비율
 * JDBC 1
 * saveAll 1.573
 * save 2.637
 */
//@SpringBootTest
public class ClientBulkInsertTest {
    private final int loop = 10;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    ClientBulkRepository clientBulkRepository;

    @Autowired
    EntityManager em;

    private Group group;
    long start, end, runningTimeNano;

    @BeforeEach
    void beforeEach() {
        group = TestEntityCreator.createGroupWithUser();
        em.persist(group.getUser());
        em.persist(group);
        em.flush();
    }

//    @Test
    @Transactional
    void bulkInsertWithJpaSaveEachTest() {
        start = System.nanoTime();

        /*========================================*/
        for (int i = 0; i < loop; i++) {
            Client client = TestEntityCreator.createClient(i, group);
            clientRepository.save(client);
        }
        /*========================================*/

        end = System.nanoTime();
        runningTimeNano = end - start;
        double runningTimeMillis = runningTimeNano / 1_000_000.0;
        System.out.println("동작 시간: " + runningTimeMillis);
    }

//    @Test
    @Transactional
    void bulkInsertWithJpaSaveAllTest() {
        start = System.nanoTime();

        /*========================================*/
        List<Client> list = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            Client client = TestEntityCreator.createClient(i, group);
            list.add(client);
        }
        clientRepository.saveAll(list);
        /*========================================*/

        end = System.nanoTime();
        runningTimeNano = end - start;
        double runningTimeMillis = runningTimeNano / 1_000_000.0;
        System.out.println("동작 시간: " + runningTimeMillis);
    }

//    @Test
    @Transactional
    void bulkInsertWithJDBCTest() {
        start = System.nanoTime();

        /*========================================*/
        List<Client> list = new ArrayList<>();
        for (int i = 0; i < loop; i++) {
            Client client = TestEntityCreator.createClient(i, group);
            list.add(client);
        }
        clientBulkRepository.saveClientWithGroup(group, list);
        /*========================================*/

        end = System.nanoTime();
        runningTimeNano = end - start;
        double runningTimeMillis = runningTimeNano / 1_000_000.0;
        System.out.println("동작 시간: " + runningTimeMillis);
    }

}
