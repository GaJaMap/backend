package com.map.gaja;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.client.domain.model.ClientAddress;
import com.map.gaja.client.domain.model.ClientImage;
import com.map.gaja.client.domain.model.ClientLocation;
import com.map.gaja.group.domain.model.Group;
import com.map.gaja.user.domain.model.User;
import org.springframework.mock.web.MockMultipartFile;

public class TestEntityCreator {

    public static Client createClient(int sigIdx, Group group, User user) {
        String sig = sigIdx+""+sigIdx;
        double pointSig = 0.003;

        String name = "사용자 " + sig;
        String phoneNumber = "010-1111-" + sig;
        ClientAddress address = new ClientAddress("address " + sig, "detail " + sig);
        ClientLocation location = new ClientLocation(35d + pointSig * sigIdx, 125.0d + pointSig * sigIdx);
        return Client.createWithoutImage(name, phoneNumber, address, location, group, user);
    }

    public static Client createClientWithImage(String clientName, Group existingGroup, ClientImage existingImage, User user) {
        return Client.createWithImage(
                clientName, null,
                new ClientAddress("Test Main Address", "Test Detail Address"),
                new ClientLocation(35d, 125d),
                existingImage, existingGroup, user
        );
    }

    public static Group createGroup(User user, String groupName) {
        return Group.builder()
                .name(groupName)
                .user(user)
                .clientCount(0)
                .isDeleted(false)
                .build();
    }

    public static Group createGroup(User user, Long groupId, String groupName, Integer clientCount) {
        return Group.builder()
                .id(groupId)
                .name(groupName)
                .user(user)
                .clientCount(clientCount)
                .isDeleted(false)
                .build();
    }

    public static User createUser(String userEmail) {
        return new User(userEmail);
    }

    private static final String testEmail = "test@example.com";
    private static final String groupName = "Test Group";
    private static final String oriFilaName = "testImage.png";


    public static Group createGroupWithUser() {
        User user = createUser(testEmail);
        Group group = createGroup(user, groupName);
        return group;
    }

    public static ClientImage createClientImage() {
        return createClientImage(testEmail);
    }

    public static ClientImage createClientImage(String email) {
        MockMultipartFile image = createMockFile();
        return ClientImage.create(email, image);
    }

    public static MockMultipartFile createMockFile() {
        return createMockFile(oriFilaName);
    }

    public static MockMultipartFile createMockFile(String fileName) {
        byte[] somethingImage = {1, 1};
        MockMultipartFile image = new MockMultipartFile(fileName, fileName, null, somethingImage);
        return image;
    }
}
