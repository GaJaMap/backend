package com.map.gaja.bundle.application;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.infrastructure.BundleQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BundleAccessVerifyService {
    private final BundleQueryRepository bundleQueryRepository;

    public void verifyBundleAccess(long bundleId, String userEmail) {
        if(bundleQueryRepository.hasNoBundleByUser(bundleId, userEmail)) {
            throw new BundleNotFoundException();
        }
    }
}
