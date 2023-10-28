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

    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String memo;

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

    /**
     * 간단 bulk 연산용 - 카카오, 전화번호부로 등록
     */
    public Client(String name, String phoneNumber, Group group) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateGroup(group);
    }

    /**
     * 이미지 없는 고객 등록
     */
    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Group group) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        updateGroup(group);
        this.clientImage = null;
    }

    /**
     * 이미지와 함께 고객 등록
     */
    public Client(String name, String phoneNumber, ClientAddress address, ClientLocation location, Group group, ClientImage clientImage) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        updateLocation(location, address);
        updateGroup(group);
        this.clientImage = clientImage;
    }

    public void updateClientField(String name, String phoneNumber, ClientAddress address, ClientLocation location) {
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

    public void updateImage(ClientImage newImage) {
        removeClientImage();
        this.clientImage = newImage;
    }

    public void removeClientImage() {
        if (clientImage == null) {
            return;
        }

        clientImage.delete();
        this.clientImage = null;
    }
}
