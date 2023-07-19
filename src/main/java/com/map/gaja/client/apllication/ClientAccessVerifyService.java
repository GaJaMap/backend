package com.map.gaja.client.apllication;

import com.map.gaja.client.presentation.dto.ClientListAccessCheckDto;
import com.map.gaja.group.application.GroupAccessVerifyService;
import com.map.gaja.client.infrastructure.repository.ClientQueryRepository;
import com.map.gaja.client.presentation.dto.ClientAccessCheckDto;
import com.map.gaja.client.domain.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClientAccessVerifyService {
    private final ClientQueryRepository clientQueryRepository;
    private final GroupAccessVerifyService groupAccessVerifyService;

    public void verifyClientAccess(ClientAccessCheckDto accessRequest) {
        String userEmail = accessRequest.getUserEmail();
        long groupId = accessRequest.getGroupId();
        long clientId = accessRequest.getClientId();

        groupAccessVerifyService.verifyGroupAccess(groupId, userEmail);
        if (clientQueryRepository.hasNoClientByGroup(groupId, clientId)) {
            throw new ClientNotFoundException();
        }
    }
    public void verifyClientListAccess(ClientListAccessCheckDto accessRequest) {
        String userEmail = accessRequest.getUserEmail();
        long groupId = accessRequest.getGroupId();
        List<Long> clientIds = accessRequest.getClientId();

        groupAccessVerifyService.verifyGroupAccess(groupId, userEmail);
        long matchingCount = clientQueryRepository.findMatchingClientCountInGroup(groupId, clientIds);
        if (areSomeClientsNotFound(clientIds, matchingCount)) {
            throw new ClientNotFoundException();
        }
    }

    private static boolean areSomeClientsNotFound(List<Long> clientIds, long matchingCount) {
        return clientIds.size() != matchingCount;
    }

}
