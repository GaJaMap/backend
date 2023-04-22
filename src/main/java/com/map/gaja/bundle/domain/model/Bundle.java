package com.map.gaja.bundle.domain.model;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.user.domain.model.User;
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
public class Bundle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bundle_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private Integer clientCount;

    @OneToMany(mappedBy = "bundle")
    private List<Client> clients;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
