package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Transactional
class ClientRepositoryCustomTest {

    @Autowired
    ClientRepository clientRepository;

    @BeforeEach
    void before() {
        List<Client> clientList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            String sig = i+""+i;
            String name = "사용자 " + sig;
            String phoneNumber = "010-1111-" + sig;
            ClientAddress address = new ClientAddress("aaa" + sig, "bbb" + sig, "ccc" + sig, "ddd"+sig);
            ClientLocation location = new ClientLocation(35d+0.003*i,  125.0d+0.003*i);
            Client client = new Client(name, phoneNumber, address, location, null);
            clientList.add(client);
        }
        clientRepository.saveAll(clientList);
    }

    @Test
    void keywordSearchTest() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<ClientResponse> result = clientRepository.findClientByConditions(null,"사용자", pageable);
        List<ClientResponse> content = result.getContent();
        System.out.println("result = " + result);
        for (ClientResponse client : content) {
            System.out.println("client = " + client);
        }
    }

    @Test
    void ss() {
        System.out.println("sdasd");
    }

}