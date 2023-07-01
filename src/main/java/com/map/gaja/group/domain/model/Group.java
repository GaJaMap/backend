package com.map.gaja.group.domain.model;

import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import com.map.gaja.user.domain.model.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "groups")
public class Group extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private Integer clientCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateName(String name) {
        this.name = name;
    }

    /**
     * Client 클래스 내부에서 사용할 메소드.
     * Client 클래스 내부 이외 호출은 자제.
     */
    public void increaseClientCount() {
        clientCount++;
    }

    public void decreaseClientCount() {
        clientCount--;
    }
}
