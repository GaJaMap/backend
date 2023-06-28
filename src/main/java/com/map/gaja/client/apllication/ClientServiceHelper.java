package com.map.gaja.client.apllication;

import com.map.gaja.bundle.domain.exception.BundleNotFoundException;
import com.map.gaja.bundle.infrastructure.BundleQueryRepository;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.presentation.exception.ClientNotFoundException;
import com.map.gaja.user.domain.exception.UserNotFoundException;
import com.map.gaja.user.domain.model.User;
import com.map.gaja.user.infrastructure.UserRepository;

public class ClientServiceHelper {
    public static void verifyClientAccess (
            BundleQueryRepository bundleQueryRepository,
            ClientQueryRepository clientQueryRepository,
            ClientAccessCheckDto accessRequest
    ) {
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
