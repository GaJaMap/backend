package com.map.gaja.user.domain.model;

import com.map.gaja.bundle.domain.model.Bundle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@Getter
public class User {

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

    @OneToMany(mappedBy = "user")
    private List<Bundle> bundles;
}
