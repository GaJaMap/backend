package com.map.gaja.client.infrastructure.repository;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.client.domain.model.QClient;
import com.map.gaja.client.presentation.dto.request.NearbyClientSearchRequest;
import com.map.gaja.client.presentation.dto.response.ClientResponse;
import com.map.gaja.client.presentation.dto.subdto.AddressDto;
import com.map.gaja.client.presentation.dto.subdto.LocationDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static com.map.gaja.client.domain.model.QClient.*;

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

    @PersistenceContext
    EntityManager em;

    @Test
    void distanceQueryTest() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        NearbyClientSearchRequest request = new NearbyClientSearchRequest(new LocationDto(35d, 125d), 3000d);

        NumberExpression<Double> distance = Expressions.numberTemplate(Double.class,"ST_Distance_Sphere({0}, {1})",
                        Expressions.stringTemplate("POINT({0}, {1})",
                                request.getLocation().getLongitude(),
                                request.getLocation().getLatitude()
                        ),
                        Expressions.stringTemplate("POINT({0}, {1})",
                                client.location.longitude,
                                client.location.latitude
                        )
                );


        List<Client> result = query
                .select(client)
                .from(client)
                .where(distance.loe(request.getRadius()))
                .orderBy(distance.asc())
                .fetch();

        for (Client client1 : result) {
            ClientLocation location = client1.getLocation();
            System.out.println("거리:" +distance + " (" + location.getLongitude() + "," + location.getLatitude() + ")");
        }
    }

    @Test
    void test2() {
        JPAQueryFactory query = new JPAQueryFactory(em);
        NearbyClientSearchRequest request = new NearbyClientSearchRequest(new LocationDto(35d, 125d), 3000d);

        NumberExpression<Double> distance = Expressions.numberTemplate(Double.class,"ST_Distance_Sphere({0}, {1})",
                Expressions.stringTemplate("POINT({0}, {1})",
                        request.getLocation().getLongitude(),
                        request.getLocation().getLatitude()
                ),
                Expressions.stringTemplate("POINT({0}, {1})",
                        client.location.longitude,
                        client.location.latitude
                )
        );

        List<Double> result = query.select(distance.doubleValue())
                .from(client)
                .fetch();

        for (Double aDouble : result) {
            System.out.println("aDouble = " + aDouble);
        }
    }

    @Test
    void test3() {
        JPAQueryFactory query = new JPAQueryFactory(em);

        NumberExpression<Long> distance = Expressions.numberTemplate(Long.class,"count(*)");
        List<Long> result = query.select(distance)
                .from(client)
                .fetch();

        for (Long aLong : result) {
            System.out.println("aLong = " + aLong);
        }
    }

}