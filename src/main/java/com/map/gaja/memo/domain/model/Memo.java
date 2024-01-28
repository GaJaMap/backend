package com.map.gaja.memo.domain.model;

import com.map.gaja.client.domain.model.Client;
import com.map.gaja.user.domain.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memo_id")
    private Long id;

    @Column(length = 100)
    private String message;

    @Enumerated(EnumType.STRING)
    private MemoType memoType;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Memo(Long id, String message, MemoType memoType, LocalDateTime createdAt, Client client, User user) {
        this.id = id;
        this.message = message;
        this.memoType = memoType;
        this.createdAt = createdAt;
        this.client = client;
        this.user = user;
    }

    public Memo(String message, MemoType memoType, Client client, User user) {
        this.message = message;
        this.memoType = memoType;
        this.client = client;
        this.user = user;
    }
}
