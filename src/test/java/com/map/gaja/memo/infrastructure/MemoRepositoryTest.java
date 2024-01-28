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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

        Memo memo = new Memo(null, MemoType.CALL, client, user);
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

        Memo memo = new Memo(null, MemoType.CALL, client, user);
        memoRepository.save(memo);

        // when, then
        assertThat(memoRepository.findByIdAndClient(memo.getId(), user.getId() + 1).isEmpty())
                .isTrue();
    }

    @Test
    @DisplayName("메모를 최신순으로 조회한다.")
    void findPageByClientId() {
        // given
        User user = TestEntityCreator.createUser("test@gmail.com");
        Group group = TestEntityCreator.createGroup(user, "group");
        Client client = TestEntityCreator.createClient(0, group, user);
        Memo memo = new Memo(null, MemoType.CALL, client, user);
        Memo memo2 = new Memo(null, MemoType.NAVIGATION, client, user);
        em.persist(user);
        em.persist(group);
        em.persist(client);
        em.persist(memo);
        em.persist(memo2);
        em.flush();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Order.desc("id")));

        // when
        Slice<Memo> memos = memoRepository.findPageByClientId(client.getId(), user.getId(), pageable);

        // then
        assertThat(memos.getSize()).isEqualTo(2);
        assertThat(memos.getContent().get(0)).isEqualTo(memo2);
        assertThat(memos.getContent().get(1)).isEqualTo(memo);
    }

    @Test
    @DisplayName("메모를 삭제한다.")
    void deleteByIdAndUser() {
        // given
        User user = TestEntityCreator.createUser("test@gmail.com");
        Group group = TestEntityCreator.createGroup(user, "group");
        Client client = TestEntityCreator.createClient(0, group, user);
        Memo memo = new Memo(null, MemoType.CALL, client, user);
        em.persist(user);
        em.persist(group);
        em.persist(client);
        em.persist(memo);
        em.flush();

        // when, then
        assertDoesNotThrow(() -> memoRepository.deleteByIdAndUser(memo.getId(), user.getId()));
    }
}