package com.map.gaja.client.domain.model;

import com.map.gaja.client.event.ClientGroupUpdatedEvent;
import com.map.gaja.client.event.ClientImageDeletedEvent;
import com.map.gaja.client.event.GroupClientAddedEvent;
import com.map.gaja.global.event.Events;
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

    /** 전화번호를 통한 등록 시 */
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

        Events.raise(new GroupClientAddedEvent(group.getId(), user));
        return client;
    }

    /** 엑셀 파일 or 이미지 없는 사용자 등록 시 */
    public static Client createWithoutImage(
            String name, String phoneNumber,
            ClientAddress address, ClientLocation location,
            Group group, User user
    ) {
        Client client = create(name, phoneNumber, group, user);
        client.updateLocation(location, address);
        return client;
    }

    /** 이미지 있는 사용자 등록 시 */
    public static Client createWithImage(
            String name, String phoneNumber,
            ClientAddress address, ClientLocation location,
            ClientImage clientImage,
            Group group, User user
    ) {
        Client client = createWithoutImage(name, phoneNumber, address, location, group, user);
        client.clientImage = clientImage;
        return client;
    }

    private static void validateRequiredFields(String name, Group group, User user) {
        if (name == null || group == null || user == null) {
            throw new IllegalArgumentException();
        }
    }

    public void updateWithoutClientImage(String name, String phoneNumber, ClientAddress address, ClientLocation location) {
        updateName(name);
        updatePhoneNumber(phoneNumber);
        updateLocation(location, address);
    }

    public void updateGroup(Group changedGroup) {
        if(group == changedGroup)
            return;

        Events.raise(new ClientGroupUpdatedEvent(this.group.getId(), changedGroup.getId(), user));
        this.group = changedGroup;
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
        removeImage();
        this.clientImage = image;
    }

    public void removeImage() {
        if (clientImage == null) {
            return;
        }

        Events.raise(new ClientImageDeletedEvent(this.clientImage));
        this.clientImage = null;
    }
}
