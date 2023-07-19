package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.ClientLimitExceededException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.Authority;
import org.springframework.stereotype.Service;

@Service
public class IncreasingClientService {

    public void increase(Group group, Authority authority, int count) {
        if (isCreateClient(authority.getClientLimitCount(), group.getClientCount(), count)) {
            group.increaseClientCount(count);
            return;
        }

        throw new ClientLimitExceededException(authority.name(), group.getClientCount());
    }

    private boolean isCreateClient(int clientLimitCount, int currentClientCount, int increaseInClient) {
        if (clientLimitCount >= (currentClientCount + increaseInClient)) {
            return true;
        }
        return false;
    }
}