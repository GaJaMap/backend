package com.map.gaja.user.domain.model;

import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "users")
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
    private LocalDateTime lastLoginDate;

    private Long referenceGroupId;

    private Boolean active;

    public User(String email) {
        this.email = email;
        authority = Authority.FREE;
        groupCount = 0;
        lastLoginDate = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        active = true;
    }

    public void checkCreateGroupPermission() {
        if (isGroupCreationLimitExceeded()) {
            throw new GroupLimitExceededException(authority.name(), authority.getGroupLimitCount());
        }
    }

    private boolean isGroupCreationLimitExceeded() {
        return authority.getGroupLimitCount() <= groupCount;
    }

    public void increaseGroupCount() {
        groupCount++;
    }

    public void accessGroup(Long groupId) {
        this.referenceGroupId = groupId;
    }

    public void withdrawal() {
        active = false;
    }

    public void decreaseGroupCount() {
        groupCount--;
    }

    /**
     * hour, min, sec을 제외한 yyyy-MM-dd 날짜가 다르면 최근 접속 일을 update
     */
    public void updateLastLoginDate() {
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        if (isDifferentDate(currentDateTime)) {
            this.lastLoginDate = currentDateTime;
        }
    }

    private boolean isDifferentDate(LocalDateTime currentDateTime) {
        if (!lastLoginDate.toLocalDate().isEqual(currentDateTime.toLocalDate())) {
            return true;
        }
        return false;
    }

}
