package com.map.gaja.user.domain.model;

import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import com.map.gaja.global.event.Events;
import com.map.gaja.user.domain.exception.GroupLimitExceededException;
import com.map.gaja.user.event.WithdrawnEvent;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.map.gaja.user.constant.UserConstant.DATE_FORMAT;

@Entity
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

    @Builder
    private User(Long id, String email, Integer groupCount, Authority authority, LocalDateTime lastLoginDate, Long referenceGroupId, Boolean active) {
        this.id = id;
        this.email = email;
        this.groupCount = groupCount;
        this.authority = authority;
        this.lastLoginDate = lastLoginDate;
        this.referenceGroupId = referenceGroupId;
        this.active = active;
    }

    public User(String email) {
        this.email = email;
        authority = Authority.FREE;
        groupCount = 0;
        lastLoginDate = LocalDateTime.now();
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
        Events.raise(new WithdrawnEvent(email));
    }

    public void decreaseGroupCount() {
        groupCount--;
    }

    /**
     * hour, min, sec을 제외한 yyyy-MM-dd 날짜가 다르면 최근 접속 일을 update
     */
    public void updateLastLoginDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();

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

    public String getFormattedDateAsString() {
        return getCreatedAt().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

}
