package com.map.gaja.client.domain.model;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.global.auditing.entity.BaseTimeEntity;
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

    @Column(nullable = false)
    private String phoneNumber;

    @Embedded
    private ClientAddress address;

    @Embedded
    private ClientLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "client_image_id")
    private ClientImage clientImage;

    public Client(String name, String phoneNumber, Group group) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        setGroup(group);
    }

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Group group) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        setGroup(group);
        this.clientImage = null;
    }

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Group group, ClientImage clientImage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        setGroup(group);
        this.clientImage = clientImage;
    }

    public void updateWithoutImage(String name, String phoneNumber, ClientAddress address, ClientLocation location) {
        updateName(name);
        updatePhoneNumber(phoneNumber);
        updateLocation(location, address);
        removeAndUpdateGroup(group);
    }

    public void updateGroup(Group group) {
        this.group = group;
    }

    public void removeGroup() {
        group.decreaseClientCount(1);
        group = null;
    }

    private void setGroup(Group group) {
        this.group = group;
        group.increaseClientCount(1);
    }

    private void removeAndUpdateGroup(Group group) {
        removeGroup();
        setGroup(group);
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
