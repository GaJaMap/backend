package com.map.gaja.group.domain.model;

import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import com.map.gaja.global.event.Events;
import com.map.gaja.group.domain.exception.ClientNotDeletedException;
import com.map.gaja.group.event.GroupCreatedEvent;
import com.map.gaja.user.domain.model.User;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "GROUP_SET")
public class Group extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private Integer clientCount;

    @Column(nullable = false)
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Group(Long id, String name, Integer clientCount, Boolean isDeleted, User user) {
        this.id = id;
        this.name = name;
        this.clientCount = clientCount;
        this.isDeleted = isDeleted;
        this.user = user;
    }

    public Group(String name, User user) {
        this.name = name;
        this.user = user;
        clientCount = 0;
        isDeleted = false;
        Events.raise(new GroupCreatedEvent(user));
    }

    public void updateName(String name) {
        this.name = name;
    }

    /**
     * 그룹삭제시 이미지 삭제 배치 작업을 위해 삭제 필드를 true 상태로 변경
     */
    public void remove() {
        isDeleted = true;
    }

    /**
     * Client 클래스 내부에서 사용할 메소드.
     * Client 클래스 내부 이외 호출은 자제.
     */
    public void increaseClientCount(int count) {
        clientCount += count;
    }

    public void decreaseClientCount(int decreaseInCount) {
        if (isClientUnreducible(decreaseInCount)) {
            throw new ClientNotDeletedException();
        }
        clientCount -= decreaseInCount;
    }

    private boolean isClientUnreducible(int decreaseInCount) {
        return clientCount < decreaseInCount;
    }

}
