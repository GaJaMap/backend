package com.map.gaja.user.domain.model;

import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer groupCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime lastLoginDate;

    private Integer referenceGroupId;

    public void checkCreateGroupPermission() {
        if (authority.getLimitCount() <= groupCount) {
            throw new GroupLimitExceededException(authority.name(), authority.getLimitCount());
        }
    }

    public void increaseGroupCount() {
        groupCount++;
    }
}
