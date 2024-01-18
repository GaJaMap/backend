package com.map.gaja.memo.infrastructure;

import com.map.gaja.TestEntityCreator;
import com.map.gaja.client.domain.model.Client;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.memo.domain.model.Memo;
import com.map.gaja.memo.domain.model.MemoType;
import com.map.gaja.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemoRepositoryTest {
    @Autowired
    MemoRepository memoRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("User가 가진 Memo를 조회한다.")
    void findByIdAndClientSuccess() {
        // given
        User user = TestEntityCreator.createUser("test@gmail.com");
        Group group = TestEntityCreator.createGroup(user, "group");
        Client client = TestEntityCreator.createClient(0, group, user);
        em.persist(user);
        em.persist(group);
        em.persist(client);
        em.flush();
        em.clear();

        Memo memo = new Memo(null, MemoType.CALL, client);
        memoRepository.save(memo);

        // when, then
        assertThat(memoRepository.findByIdAndClient(memo.getId(), client.getId()).get())
                .isNotNull();
    }

    @Test
    @DisplayName("User가 가진 Memo의 조회 실패한다.")
    void findByIdAndClientFail() {
        // given
        User user = TestEntityCreator.createUser("test@gmail.com");
        Group group = TestEntityCreator.createGroup(user, "group");
        Client client = TestEntityCreator.createClient(0, group, user);
        em.persist(user);
        em.persist(group);
        em.persist(client);
        em.flush();
        em.clear();

        Memo memo = new Memo(null, MemoType.CALL, client);
        memoRepository.save(memo);

        // when, then
        assertThat(memoRepository.findByIdAndClient(memo.getId(), user.getId() + 1).isEmpty())
                .isTrue();
    }
}