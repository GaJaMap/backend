package com.map.gaja.client.apllication;

import com.map.gaja.bundle.application.BundleAccessVerifyService;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClientAccessVerifyService {
    private final ClientQueryRepository clientQueryRepository;
    private final BundleAccessVerifyService bundleAccessVerifyService;

    public void verifyClientAccess(ClientAccessCheckDto accessRequest) {
        String userEmail = accessRequest.getUserEmail();
        long bundleId = accessRequest.getGroupId();
        long clientId = accessRequest.getClientId();

        bundleAccessVerifyService.verifyBundleAccess(bundleId, userEmail);
        if (clientQueryRepository.hasNoClientByBundle(bundleId, clientId)) {
            throw new ClientNotFoundException(clientId);
        }
    }
}
