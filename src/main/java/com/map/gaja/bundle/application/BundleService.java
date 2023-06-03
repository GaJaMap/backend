package com.map.gaja.bundle.application;

import com.map.gaja.bundle.domain.exception.BundleDeletionException;
import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.bundle.infrastructure.BundleRepository;
import com.map.gaja.bundle.presentation.dto.request.BundleCreateRequest;
import com.map.gaja.bundle.presentation.dto.response.BundleInfo;
import com.map.gaja.bundle.presentation.dto.response.BundleResponse;
import com.map.gaja.client.infrastructure.repository.ClientRepository;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.map.gaja.user.application.UserServiceHelper.findExistingUser;

@Service
@RequiredArgsConstructor
public class BundleService {
    private final BundleRepository bundleRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @Transactional
    public void create(String email, BundleCreateRequest request) {
        User user = findExistingUser(userRepository, email);

        user.checkCreateBundlePermission();

        bundleRepository.save(createBundle(request.getName(), user));

        user.increaseBundleCount();
    }

    private Bundle createBundle(String name, User user) {
        return Bundle.builder()
                .name(name)
                .clientCount(0)
                .user(user)
                .createdDate(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
    }

    @Transactional(readOnly = true)
    public BundleResponse findBundles(String email, Pageable pageable) {
        User user = findExistingUser(userRepository, email);

        Slice<BundleInfo> bundleInfos = bundleRepository.findBundleByUserId(user.getId(), pageable);

        return new BundleResponse(bundleInfos.hasNext(), bundleInfos.getContent());
    }

    @Transactional
    public void delete(String email, Long bundleId) {
        User user = findExistingUser(userRepository, email);

        int deletedBundleCount = bundleRepository.deleteByIdAndUserId(bundleId, user.getId());

        if (deletedBundleCount == 0) { //삭제된 번들이 없다면 존재하지 않은 번들이거나 userId가 다른 번들일 가능성이 있음
            throw new BundleDeletionException();
        }

        clientRepository.deleteByBundleId(bundleId);
    }
}
