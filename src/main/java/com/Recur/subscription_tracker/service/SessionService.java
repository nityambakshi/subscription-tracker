package com.Recur.subscription_tracker.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Recur.subscription_tracker.dto.CreatedSession;
import com.Recur.subscription_tracker.model.Session;
import com.Recur.subscription_tracker.model.User;
import com.Recur.subscription_tracker.repository.SessionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Duration REUSE_GRACE = Duration.ofSeconds(10);

    @Value("${jwt.refreshTokenExpirationTime}")
    private long refreshExpirationMs;

    @Transactional
    public CreatedSession create(User user, String deviceName) {
        String raw = newRawToken();
        Instant now = Instant.now();

        Session session = Session.builder()
                .user(user)
                .refreshTokenHash(sha256(raw))
                .deviceName(deviceName)
                .createdAt(now)
                .lastUsedAt(now)
                .expiresAt(now.plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();

        sessionRepository.save(session);
        return new CreatedSession(session, raw);
    }

    /**
     * Validates and rotates a refresh token. The row stays the same, so the
     * session ID is stable across refreshes.
     *
     * noRollbackFor matters here: the reuse branch revokes sessions and then
     * throws. Without this, Spring would roll back the revocation.
     */
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public CreatedSession rotate(String rawToken) {
        String hash = sha256(rawToken);
        Instant now = Instant.now();

        Optional<Session> found = sessionRepository.findByRefreshTokenHash(hash);
        if (found.isEmpty()) {
            handleStaleToken(hash, now);
            throw new BadCredentialsException("Invalid refresh token");
        }

        Session session = found.get();

        if (session.isRevoked() || session.getExpiresAt().isBefore(now)) {
            throw new BadCredentialsException("Session expired");
        }

        String newRaw = newRawToken();
        session.setPreviousRefreshTokenHash(session.getRefreshTokenHash());
        session.setPreviousRotatedAt(now);
        session.setRefreshTokenHash(sha256(newRaw));
        session.setLastUsedAt(now);
        session.setExpiresAt(now.plusMillis(refreshExpirationMs)); // sliding window

        return new CreatedSession(session, newRaw);
    }

    @Transactional(readOnly = true)
    public boolean isActive(UUID sessionId) {
        return sessionRepository.findById(sessionId)
                .filter(s -> !s.isRevoked())
                .filter(s -> s.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }

    @Transactional
    public void revoke(String rawToken) {
        sessionRepository.findByRefreshTokenHash(sha256(rawToken))
                .ifPresent(s -> s.setRevoked(true));
    }

    @Transactional
    public void revokeAll(User user) {
        sessionRepository.findAllByUserAndRevokedFalse(user)
                .forEach(s -> s.setRevoked(true));
    }

    @Transactional
    public void revokeOne(UUID id, User user) {
        sessionRepository.findByIdAndUser(id, user)
                .ifPresent(s -> s.setRevoked(true));
    }

    public List<Session> activeSessions(User user) {
        return sessionRepository.findAllByUserAndRevokedFalse(user);
    }

    @Transactional
    public void purgeExpired() {
        sessionRepository.deleteByExpiresAtBefore(Instant.now());
    }

    private void handleStaleToken(String hash, Instant now) {
        sessionRepository.findByPreviousRefreshTokenHash(hash).ifPresent((reused) -> {
            Instant rotatedAt = reused.getPreviousRotatedAt();
            boolean withinGrace = rotatedAt != null && rotatedAt.plus(REUSE_GRACE).isAfter(now);

            if (!withinGrace) {
                revokeAll(reused.getUser());
            }
        });
    }

    private static String newRawToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
