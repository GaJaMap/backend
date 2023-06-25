package com.map.gaja.bundle.infrastructure;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.presentation.dto.response.BundleInfo;
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
class BundleRepositoryTest {
    @Autowired
    BundleRepository bundleRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("번들 조회 쿼리 테스트")
    void findBundle() {
        //given
        User user = User.builder()
                .email("test")
                .bundleCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        for (int i = 0; i < 11; i++) {
            Bundle bundle = Bundle.builder()
                    .name("test")
                    .clientCount(0)
                    .user(user)
                    .createdDate(LocalDateTime.now())
                    .build();
            bundleRepository.save(bundle);
        }
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Slice<BundleInfo> bundleInfos = bundleRepository.findBundleByUserId(user.getId(), pageable);

        //then
        assertEquals(true, bundleInfos.hasNext());
        assertEquals(10, bundleInfos.getContent().size());
        assertEquals("test", bundleInfos.getContent().get(1).getBundleName());

    }

    @Test
    @DisplayName("번들 삭제 성공")
    void deleteBundle() {
        //given
        User user = User.builder()
                .email("test")
                .bundleCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Bundle bundle = Bundle.builder()
                .name("test")
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        bundleRepository.save(bundle);

        //when
        int count = bundleRepository.deleteByIdAndUserId(bundle.getId(), user.getId());

        //then
        assertEquals(1, count);
    }

    @Test
    @DisplayName("번들 조회 성공")
    void find() {
        //given
        User user = User.builder()
                .email("test")
                .bundleCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Bundle bundle = Bundle.builder()
                .name("test")
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        bundleRepository.save(bundle);

        //when & then
        assertEquals(bundle, bundleRepository.findByIdAndUserId(bundle.getId(), user.getId()).get());
    }

    @Test
    @DisplayName("bundleId와 email로 Bundle 조회")
    void findByIdAndUserEmailTest() {
        //given
        User user = User.builder()
                .email("test")
                .bundleCount(0)
                .authority(Authority.FREE)
                .createdDate(LocalDateTime.now())
                .lastLoginDate(LocalDateTime.now())
                .build();
        userRepository.save(user);

        Bundle bundle = Bundle.builder()
                .name("test")
                .user(user)
                .clientCount(0)
                .createdDate(LocalDateTime.now())
                .build();
        bundleRepository.save(bundle);

        Bundle result = bundleRepository.findByIdAndUserEmail(bundle.getId(), user.getEmail())
                .orElseThrow(IllegalArgumentException::new);

        assertEquals(bundle, result);
    }
}