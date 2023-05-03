package com.map.gaja.client.domain.model;

import com.map.gaja.bundle.domain.model.Bundle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Getter
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Embedded
    private ClientAddress address;

    @Embedded
    private ClientLocation location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    public Client(String name) {
        // 흐름 파악을 위한 간단 생성
        this.name = name;
        this.phoneNumber = "010-1111-2222";
        this.createdDate = LocalDateTime.now();
        this.bundle = null;
    }
}
