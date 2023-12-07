package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.ClientLimitExceededException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.Authority;
import org.springframework.stereotype.Service;

@Service
public class IncreasingClientService {
    private final int ONE_INCREMENT = 1;

    public void increaseByMany(Group group, Authority authority, int count) {
        checkCanCreateClient(authority, group.getClientCount(), count);

        group.increaseClientCount(count);
    }

    public void increaseByOne(Group group, Authority authority) {
        checkCanCreateClient(authority, group.getClientCount(), ONE_INCREMENT);

        group.increaseClientCount(ONE_INCREMENT);
    }

    public void checkCanCreateClient(Authority authority, int currentClientCount, int increaseInClient) {
        if (authority.getClientLimitCount() < (currentClientCount + increaseInClient)) {
            throw new ClientLimitExceededException(authority.name(), authority.getClientLimitCount());
        }
    }
}