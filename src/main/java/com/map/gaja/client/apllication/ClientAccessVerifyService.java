package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.infrastructure.BundleQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientAccessVerifyService {
    private final ClientQueryRepository clientQueryRepository;
    private final BundleQueryRepository bundleQueryRepository;

    public void verifyClientAccess(ClientAccessCheckDto accessRequest) {
        String userEmail = accessRequest.getUserEmail();
        long bundleId = accessRequest.getBundleId();
        long clientId = accessRequest.getClientId();

        if(bundleQueryRepository.hasNoBundleByUser(bundleId, userEmail)) {
            throw new BundleNotFoundException();
        }

        if (clientQueryRepository.hasNoClientByBundle(bundleId, clientId)) {
            throw new ClientNotFoundException(clientId);
        }
    }
}
