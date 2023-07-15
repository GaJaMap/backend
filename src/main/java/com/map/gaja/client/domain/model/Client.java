package com.map.gaja.client.domain.model;

import com.map.gaja.group.domain.model.Group;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    public void updateClient(String name, String phoneNumber, ClientAddress address, ClientLocation location, Group group) {
        updateName(name);
        updatePhoneNumber(phoneNumber);
        updateLocation(location, address);
        updateGroup(group);
    }

    public void removeGroup() {
        group.decreaseClientCount();
        group = null;
    }

    private void setGroup(Group group) {
        this.group = group;
        group.increaseClientCount();
    }

    private void updateGroup(Group group) {
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

    private boolean isClientLocationNull(ClientLocation location) {
        return location.getLatitude() == null && location.getLongitude() == null;
    }
    private boolean isLocationInKorea(double latitude, double longitude) {
        return latitude >= 33 && latitude <= 38
                && longitude >= 124 && longitude <= 132;
    }
}
