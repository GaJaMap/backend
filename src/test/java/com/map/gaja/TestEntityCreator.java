package com.map.gaja;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;

public class TestEntityCreator {
    public static Client createClient(int sigIdx, Group group) {
        String sig = sigIdx+""+sigIdx;
        double pointSig = 0.003;

        String name = "사용자 " + sig;
        String phoneNumber = "010-1111-" + sig;
        ClientAddress address = new ClientAddress("address " + sig, "detail " + sig);
        ClientLocation location = new ClientLocation(35d + pointSig * sigIdx, 125.0d + pointSig * sigIdx);
        return new Client(name, phoneNumber, address, location, group);
    }

    public static Group createGroup(User user, String groupName) {
        Group createdGroup = Group.builder()
                .name(groupName)
                .user(user)
                .clientCount(0)
                .isDeleted(false)
                .build();
        return createdGroup;
    }

    public static User createUser(String userEmail) {
        return new User(userEmail);
    }

    public static Group createGroupWithUser(String userEmail, String groupName) {
        User user = createUser(userEmail);
        Group group = createGroup(user, groupName);
        return group;
    }

    public static Group createGroupWithUser() {
        String userEmail = "test@example.com";
        String groupName = "Test Group";
        User user = createUser(userEmail);
        Group group = createGroup(user, groupName);
        return group;
    }
}
