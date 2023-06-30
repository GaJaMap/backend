package com.map.gaja.client.domain.model;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Embedded
    private ClientAddress address;

    @Embedded
    private ClientLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Bundle group;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_image_id")
    private ClientImage clientImage;

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Bundle group) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        setGroup(group);
        this.clientImage = null;
    }

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Bundle group, ClientImage clientImage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        setGroup(group);
        this.clientImage = clientImage;
    }

    public void updateClient(String name, String phoneNumber, ClientAddress address, ClientLocation location, Bundle bundle) {
        updateName(name);
        updatePhoneNumber(phoneNumber);
        updateLocation(location, address);
        updateBundle(bundle);
    }

    public void updateClient(String name, String phoneNumber, ClientAddress address, ClientLocation location, Bundle bundle, ClientImage clientImage) {
        updateName(name);
        updatePhoneNumber(phoneNumber);
        updateLocation(location, address);
        updateBundle(bundle);
        updateClientImage(clientImage);
    }

    public void removeBundle() {
        group.decreaseClientCount();
        group = null;
    }

    private void setGroup(Bundle group) {
        this.group = group;
        group.increaseClientCount();
    }

    private void updateBundle(Bundle bundle) {
        removeBundle();
        setGroup(bundle);
    }

    private void updateName(String name) {
        this.name = name;
    }

    private void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void updateClientImage(ClientImage clientImage) {
        this.clientImage = clientImage;
    }

    private void updateLocation(ClientLocation location, ClientAddress address) {
        validateLocation(location);
        this.location = location;
        this.address = address;
    }

    private void validateLocation(ClientLocation location) {
        if (isClientLocationNull(location)) {
            return;
        }
        // 위도, 경도 중 하나라도 null이 아니라면 Korea 범위에 있어야 함.

        if (!isLocationInKorea(location.getLatitude(), location.getLongitude())) {
            throw new LocationOutsideKoreaException(location.getLatitude(), location.getLatitude());
        }
    }

    private boolean isClientLocationNull(ClientLocation location) {
        return location.getLatitude() == null && location.getLongitude() == null;
    }
    private boolean isLocationInKorea(double latitude, double longitude) {
        return latitude >= 33 && latitude <= 38
                && longitude >= 124 && longitude <= 132;
    }
}
