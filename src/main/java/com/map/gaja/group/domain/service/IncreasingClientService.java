package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.ClientLimitExceededException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.Authority;
import org.springframework.stereotype.Service;

@Service
public class IncreasingClientService {

    public void increase(Group group, Authority authority, int count) {
        checkCanCreateClient(authority, group.getClientCount(), count);

        group.increaseClientCount(count);
    }

    public void checkCanCreateClient(Authority authority, int currentClientCount, int increaseInClient) {
        if (authority.getClientLimitCount() < (currentClientCount + increaseInClient)) {
            throw new ClientLimitExceededException(authority.name(), authority.getClientLimitCount());
        }
    }
}