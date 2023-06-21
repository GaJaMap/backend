package com.map.gaja.user.domain.model;

import com.map.gaja.global.auditing.entity.BaseTimeEntity;
import com.map.gaja.user.domain.exception.BundleLimitExceededException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Getter
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer bundleCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime lastLoginDate;

    public void checkCreateBundlePermission() {
        if (authority.getLimitCount() <= bundleCount) {
            throw new BundleLimitExceededException(authority.name(), authority.getLimitCount());
        }
    }

    public void increaseBundleCount() {
        bundleCount++;
    }
}
