package com.map.gaja.client.domain.model;

import com.map.gaja.bundle.domain.model.Bundle;
import com.map.gaja.client.domain.exception.LocationOutsideKoreaException;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Client {
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
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_img")
    private ClientImage clientImage;

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Bundle bundle) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        this.bundle = bundle;
    }

    public void updateClient(String name, String phoneNumber, ClientAddress address, ClientLocation location, Bundle bundle) {
        updateName(name);
        updatePhoneNumber(phoneNumber);
        updateLocation(location, address);
        updateBundle(bundle);
    }

    private void updateBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    private void updateName(String name) {
        this.name = name;
    }

    private void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private void updateLocation(ClientLocation location, ClientAddress address) {
        validateLocation(location);
        this.location = location;
        this.address = address;
    }

    private void validateLocation(ClientLocation location) {
        if (!isLocationInKorea(location.getLatitude(), location.getLongitude())) {
            throw new LocationOutsideKoreaException(location.getLatitude(), location.getLatitude());
        }
    }

    private boolean isLocationInKorea(double latitude, double longitude) {
        return latitude >= 33 && latitude <= 38
                && longitude >= 124 && longitude <= 132;
    }
}
