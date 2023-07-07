package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.exception.ClientLimitExceededException;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.Authority;
import org.springframework.stereotype.Service;

@Service
public class IncreasingClientService {

    public void increase(Group group, Authority authority) {
        if(authority.getClientLimitCount()<group.getClientCount()){
            throw new ClientLimitExceededException(authority.name(), group.getClientCount());
        }

        group.increaseClientCount();
    }

}