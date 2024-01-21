package com.map.gaja.client.domain.model;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import com.map.gaja.user.domain.model.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String phoneNumber;

    @Embedded
    private ClientAddress address;

    @Embedded
    private ClientLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_image_id")
    private ClientImage clientImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Client create(
            String name, String phoneNumber,
            Group group, User user
    ) {
        validateRequiredFields(name, group, user);

        Client client = new Client();
        client.name = name;
        client.phoneNumber = phoneNumber;
        client.group = group;
        client.user = user;
        return client;
    }

    public static Client createWithLocation(
            String name, String phoneNumber,
            ClientAddress address, ClientLocation location,
            Group group, User user
    ) {
        Client client = create(name, phoneNumber, group, user);
        client.updateLocation(location, address);
        return client;
    }

    public static Client createWithLocationAndImage(
            String name, String phoneNumber,
            ClientAddress address, ClientLocation location,
            ClientImage clientImage,
            Group group, User user
    ) {
        Client client = createWithLocation(name, phoneNumber, address, location, group, user);
        client.clientImage = clientImage;
        return client;
    }

    private static void validateRequiredFields(String name, Group group, User user) {
        if (name == null || group == null || user == null) {
            throw new IllegalArgumentException();
        }
    }

    public Client(String name, String phoneNumber, Group group, User user) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateGroup(group);
        this.user = user;
    }

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Group group, User user) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        updateGroup(group);
        this.clientImage = null;
        this.user = user;
    }

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Group group, ClientImage clientImage, User user) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        updateGroup(group);
        this.clientImage = clientImage;
        this.user = user;
    }

    public void updateWithoutClientImage(String name, String phoneNumber, ClientAddress address, ClientLocation location) {
        updateName(name);
        updatePhoneNumber(phoneNumber);
        updateLocation(location, address);
    }

    public void updateGroup(Group group) {
        this.group = group;
    }

    private void updateName(String name) {
        this.name = name;
    }

    private void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void updateLocation(ClientLocation location, ClientAddress address) {
        this.location = location;
        this.address = address;
    }

    public void updateImage(ClientImage image) {
        this.clientImage = image;
    }

    public void removeClientImage() {
        if (clientImage == null) {
            return;
        }

        clientImage.delete();
        this.clientImage = null;
    }
}
