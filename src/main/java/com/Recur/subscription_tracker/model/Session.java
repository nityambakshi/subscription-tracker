package com.Recur.subscription_tracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class   Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    // SHA-256 of the current refresh token. The raw token is never stored.
    @Column(nullable = false, unique = true, length = 64)
    private String refreshTokenHash;

    // Hash of the previous refresh token. Presenting it again means reuse.
    @Column(length = 64)
    private String previousRefreshTokenHash;

    private String deviceName;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastUsedAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;
}