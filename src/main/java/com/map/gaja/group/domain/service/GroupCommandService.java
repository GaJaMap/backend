package com.map.gaja.group.domain.service;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class GroupCommandService {

    public Group create(String groupName, User user) {
        user.checkCreateGroupPermission();

        return new Group(groupName, user);
    }
}
