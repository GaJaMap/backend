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

    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Bundle bundle) {
        this.name = name;
        this.phoneNumber = phoneNumber;

        if(hasLocation(location))
            changeLocation(location, address);

        this.bundle = bundle;
    }

    private boolean hasLocation(ClientLocation location) {
        return location != null && (location.getLatitude() != null || location.getLongitude() != null);
    }

    public void changeLocation(ClientLocation location, ClientAddress address) {
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
